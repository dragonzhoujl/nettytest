package com.qiyue.netty.chat;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private final String url;
	
	public HttpFileServerHandler(String url) {
		this.url=url;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		if(!req.decoderResult().isSuccess()) {
			sendError(ctx,HttpResponseStatus.BAD_REQUEST);
			return;
		}
		if(req.method()!=HttpMethod.GET) {
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
			return;
		}
		final String uri=req.uri();
		final String path=sanitizeUri(uri);
		if(path==null) {
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		File file=new File(path);
		if(!file.exists()||file.isHidden()) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		if(file.isDirectory()) {
			if(uri.endsWith("/")) {
				sendList(ctx,file);
			}else {
				sendRedirect(ctx,uri+"/");
			}
			return;
		}
		if(!file.isFile()) {
			
			sendError(ctx, HttpResponseStatus.FORBIDDEN);
			return;
		}
		RandomAccessFile randomAccessFile=null;
		try {
			randomAccessFile=new RandomAccessFile(file, "r");
		} catch (Exception e) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		long fileLength=randomAccessFile.length();
		HttpResponse response=new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		//sendContentLength(response,fileLength);
		sendContentTypeHeader(response,file);
		if(HttpHeaderUtil.isKeepAlive(req)) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			
		}
		ctx.write(response);
		ChannelFuture sendFileFuture;
		sendFileFuture=ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8096),ctx.newProgressivePromise() );
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			
			@Override
			public void operationComplete(ChannelProgressiveFuture arg0) throws Exception {
				System.err.println("Transfer complete");
				
			}
			
			@Override
			public void operationProgressed(ChannelProgressiveFuture arg0, long progress, long total) throws Exception {
				if(total<0) {
					System.err.println("Transfer progress :"+progress);
				}else {
					System.err.println("Transfer progress :"+progress+"/"+total);

				}
				
			}
		});
		ChannelFuture lastContentFuture =ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if(!HttpHeaderUtil.isKeepAlive(req)) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
		
		
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(ctx.channel().isActive()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
	private final static Pattern INSECURE_URI=Pattern.compile(".*[<>&\"].*"); 
	private final static Pattern ALLOWED_FILE_NAME=Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*"); 

	private void sendContentTypeHeader(HttpResponse response, File file) {
		 MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
	        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
		
	}

	private void sendContentLength(HttpResponse response, long fileLength) {
		// TODO Auto-generated method stub
		
	}

	private void sendRedirect(ChannelHandlerContext ctx, String newUri) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
//      response.headers().set("LOCATIN", newUri);
      response.headers().set(HttpHeaderNames.LOCATION, newUri);
      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

	private static void sendList(ChannelHandlerContext ctx, File dir) {
		FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
		String dirPath = dir.getPath();
        StringBuilder buf = new StringBuilder();
        
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("目录:");
        buf.append("</title></head><body>\r\n");
        
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<a href=\" ../\")..</a></li>\r\n");
        for (File f : dir.listFiles()) {
            if(f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            
            buf.append("<li>链接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);  
        response.content().writeBytes(buffer);  
        buffer.release();  
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE); 
	}

	private String sanitizeUri(String uri) {
		try {
			uri=URLDecoder.decode(uri,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				uri=URLDecoder.decode(uri,"ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new Error();
			}
			e.printStackTrace();
		}
		if(!uri.startsWith(url)) {
			return null;
		}
		if(!uri.startsWith("/")) {
			return null;
		}
		uri=uri.replace('/', File.separatorChar);
		if(uri.contains(File.separator+".")||uri.contains("."+File.separator)||uri.startsWith(".")||uri.endsWith(".")||INSECURE_URI.matcher(uri).matches()) {
			return null;
		}
		return System.getProperty("user.dir")+File.separator+uri;
	}

	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, 
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

}

package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {
	private final static Logger logger = Logger.getLogger(SocketServer.class.getName());
	  
	  public static void main(String[] args) {
	    Selector selector = null;
	    ServerSocketChannel serverSocketChannel = null;
	    
	    try {
	    	//����ѡ����
		      selector = Selector.open();
		      //�򿪼����ŵ�
		      serverSocketChannel = ServerSocketChannel.open();
		      //��Ϊ������ģʽ
		      serverSocketChannel.configureBlocking(false);
		      //
		      serverSocketChannel.socket().setReuseAddress(true);
		      //�뱾�ض˿ڰ�
		      serverSocketChannel.socket().bind(new InetSocketAddress(1991));
		      //��ѡ�����󶨵������ŵ�,ֻ�з������ŵ��ſ���ע��ѡ����.����ע�������ָ�����ŵ����Խ���Accept����  
		      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	      while (selector.select() > 0) {
	        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
	        while (it.hasNext()) {
	          SelectionKey readyKey = it.next();
	          it.remove();
	          SocketChannel socketChannel = null;
	          try {
	        	  socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
	        	  logger.log(Level.INFO, "������");
	        	  //�����ļ���������D�̣��ļ���Ϊ123.txt
	        	  receiveFile(socketChannel, new File("D:/123.txt"));
	        	  //�����ļ���������E�̣��ļ���Ϊ456.txt
	        	  //ʹ��ʱȡ��ע�ͼ��ɣ���receiveFile����ͬʱ��
//	        	  sendFile(socketChannel, new File("E:/456.txt"));
	          }catch(Exception ex){
	            logger.log(Level.SEVERE, "re1", ex);
	          } finally {
	            try {
	              socketChannel.close();
	            } catch(Exception ex) {
	              logger.log(Level.SEVERE, "re2", ex);
	            }
	          }
	        }
	      }
	    } catch (ClosedChannelException ex) {
	      logger.log(Level.SEVERE, "3", ex);
	    } catch (IOException ex) {
	      logger.log(Level.SEVERE, "4", ex);
	    } finally {
	      try {
	        selector.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "5", ex);
	      }
	      try {
	        serverSocketChannel.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "6", ex);
	      }
	    }
	  }

	  private static String receiveData(SocketChannel socketChannel) throws IOException {
	    String string = null;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	    
	    try {
	      byte[] bytes;
	      int size = 0;
	      while ((size = socketChannel.read(buffer)) >= 0) {
	        buffer.flip();
	        bytes = new byte[size];
	        buffer.get(bytes);
	        baos.write(bytes);
	        buffer.clear();
	      }
	      bytes = baos.toByteArray();
	      string = new String(bytes);
	    }catch(Exception ex){
	      logger.log(Level.SEVERE, "7", ex);
	    }finally {
	      try {
	        baos.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "8", ex);
	      }
	    }
	    return string;
	  }

	  private static void sendData(SocketChannel socketChannel, String string) throws IOException {
	    byte[] bytes = string.getBytes();
	    ByteBuffer buffer = ByteBuffer.wrap(bytes);
	    socketChannel.write(buffer);
	    socketChannel.socket().shutdownOutput();
	  }
	  
	  private static void receiveFile(SocketChannel socketChannel, File file) throws IOException {
	    FileOutputStream fos = null;
	    FileChannel channel = null;
	    
	    try {
	      fos = new FileOutputStream(file);
	      channel = fos.getChannel();
	      ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

	      int size = 0;
	      while ((size = socketChannel.read(buffer)) != -1) {
	        buffer.flip();
	        if (size > 0) {
	          buffer.limit(size);
	          channel.write(buffer);
	          buffer.clear();
	        }
	      }
	    }catch(Exception ex){
	      logger.log(Level.SEVERE, "9", ex);
	    } finally {
	      try {
	        channel.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "10", ex);
	      }
	      try {
	        fos.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "11", ex);
	      }
	    }
	  }

	  private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
	    FileInputStream fis = null;
	    FileChannel channel = null;
	    try {
	      fis = new FileInputStream(file);
	      channel = fis.getChannel();
	      ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
	      int size = 0;
	      while ((size = channel.read(buffer)) != -1) {
	        buffer.rewind();
	        buffer.limit(size);
	        socketChannel.write(buffer);
	        buffer.clear();
	      }
	      socketChannel.socket().shutdownOutput();
	    }catch(Exception ex){
	      logger.log(Level.SEVERE, "12", ex);
	    } finally {
	      try {
	        channel.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "13", ex);
	      }
	      try {
	        fis.close();
	      } catch(Exception ex) {
	        logger.log(Level.SEVERE, "14", ex);
	      }
	    }
	  }
}
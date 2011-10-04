package org.ops5.messageboard;


import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.WebApplication;
import io.viper.net.server.JerseyContainerHandler;
import io.viper.net.server.chunkproxy.MappedFileServerHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import javax.ws.rs.ApplicationPath;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import io.viper.net.server.chunkproxy.FileContentInfoProvider;
import io.viper.net.server.chunkproxy.StaticFileContentInfoProvider;
import io.viper.net.server.chunkproxy.StaticFileServerHandler;
import io.viper.net.server.router.HostRouterHandler;
import io.viper.net.server.router.RouteMatcher;
import io.viper.net.server.router.RouterHandler;
import io.viper.net.server.router.UriRouteMatcher;
import io.viper.net.server.router.UriRouteMatcher.MatchMode;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.json.JSONException;


public class Server
{
  private ServerBootstrap _bootstrap;

  public static Server create(String localhostName, int port, String staticFileRoot)
    throws Exception
  {
    Server server = new Server();

    server._bootstrap =
        new ServerBootstrap(
            new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

    String localhost = String.format("http://%s:%s", localhostName, port);

    ChannelPipelineFactory pipelineFactory = new MessageBoardServerChannelPipelineFactory(staticFileRoot);

    server._bootstrap.setPipelineFactory(pipelineFactory);
    server._bootstrap.bind(new InetSocketAddress(port));

    return server;
  }

  @ApplicationPath("/mb/")
  public static class MessageBoardApplication extends PackagesResourceConfig {
    public MessageBoardApplication() {
        super("org.ops5.messageboard");
    }
  }

  private static class MessageBoardServerChannelPipelineFactory implements ChannelPipelineFactory
  {
    private final String _staticFileRoot;
    private final FileContentInfoProvider _staticFileProvider;
    private final JerseyContainerHandler _restHandler;

    public MessageBoardServerChannelPipelineFactory(
        String staticFileRoot
    )
        throws IOException, JSONException
    {
      _staticFileRoot = staticFileRoot;
      _staticFileProvider = StaticFileContentInfoProvider.create(_staticFileRoot);

      WebApplication webApplication = new WebApplicationImpl();
      ResourceConfig rc = new MessageBoardApplication();
      webApplication.initiate(rc);
      _restHandler = new JerseyContainerHandler(webApplication, rc);
    }

    @Override
    public ChannelPipeline getPipeline()
        throws Exception
    {
      LinkedHashMap<RouteMatcher, ChannelHandler> localhostRoutes = new LinkedHashMap<RouteMatcher, ChannelHandler>();
      localhostRoutes.put(new UriRouteMatcher(MatchMode.startsWith, "/mb/"), _restHandler);
      localhostRoutes.put(new UriRouteMatcher(MatchMode.startsWith, "/"), new StaticFileServerHandler(_staticFileProvider));

      ChannelPipeline lhPipeline = new DefaultChannelPipeline();
      lhPipeline.addLast("decoder", new HttpRequestDecoder());
      lhPipeline.addLast("encoder", new HttpResponseEncoder());
      lhPipeline.addLast("router", new RouterHandler("uri-handlers", localhostRoutes));

      return lhPipeline;
    }
  }
}

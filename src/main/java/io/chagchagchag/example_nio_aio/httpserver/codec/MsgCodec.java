package io.chagchagchag.example_nio_aio.httpserver.codec;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MsgCodec {
  public ByteBuffer encode(final String msg){
    var body = "<html><body><h1>OK, " + msg + "!</h1></body></html>";
    var contentLength = body.getBytes().length;

    var httpResponse = "HTTP/1.1 200 OK\n" +
        "Content-Type: text/html; charset=utf-8\n" +
        "Content-Length: " + contentLength + "\n\n" + body;

    return StandardCharsets.UTF_8.encode(httpResponse);
  }

  public String decode(final ByteBuffer buffer){
    buffer.flip();
    var httpRequest = StandardCharsets.UTF_8.decode(buffer).toString().trim();
    var firstLine = httpRequest.split("\n")[0];
    var path = firstLine.split(" ")[1];
    URI uri = URI.create(path);

    var query = uri.getQuery() == null ? "" : uri.getQuery();

    var queryMap = Arrays.stream(query.split("&"))
        .map(s -> s.split("="))
        .filter(s -> s.length == 2)
        .collect(Collectors.toMap(s -> s[0], s->s[1]));

    return queryMap.getOrDefault("name", "Anonymous");
  }
}

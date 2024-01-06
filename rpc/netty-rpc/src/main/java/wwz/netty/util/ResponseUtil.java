package wwz.netty.util;

public class ResponseUtil {
    public static Response createFailResult(String code, String msg) {
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
    public static Response createSuccesssResult(Object content) {
        Response response = new Response();
        response.setResult(content);
        return response;
    }
}

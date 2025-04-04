package org.mifos.community.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSearchRequest {
    public RequestContent request;
    public int page;
    public int size;

    @Getter
    @Setter
    public static class RequestContent {
        public String text;
    }
}

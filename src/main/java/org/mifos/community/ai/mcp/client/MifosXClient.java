/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.mifos.community.ai.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.rest.client.reactive.ClientRedirectHandler;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.mifos.community.ai.mcp.SearchParameters;

@RegisterRestClient(configKey = "mifosx")
@ClientHeaderParam(name = "Authorization", value = "{getAuthorizationHeader}")
@ClientHeaderParam(name = "fineract-platform-tenantid", value = "{getAuthorizationHeader}")
public interface MifosXClient {
    
    final Config config = ConfigProvider.getConfig();
    
    default String getAuthorizationHeader() {      
      final String apiKey = config.getConfigValue("mifos.basic.token").getValue();
      return "Basic " + apiKey;
    }
    
    default String getTenantHeader() {
      final String tenant = config.getConfigValue("mifos.tenantid").getValue();
      return tenant;
    }

    @GET
    @Path("/fineract-provider/api/v1/search")
    JsonNode getClientDetails(@BeanParam SearchParameters filterParams);
}
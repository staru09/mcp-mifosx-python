/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mifos.community.ai.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.mifos.community.ai.mcp.SearchParameters;
import org.mifos.community.ai.mcp.dto.LegalForm;

@RegisterRestClient(configKey = "mifosx")
@ClientHeaderParam(name = "Authorization", value = "{getAuthorizationHeader}")
@ClientHeaderParam(name = "fineract-platform-tenantid", value = "{getTenantHeader}")
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
    
    @GET
    @Path("/fineract-provider/api/v1/offices")
    JsonNode getOffices();
    
    JsonNode getLegalForms();
    
}
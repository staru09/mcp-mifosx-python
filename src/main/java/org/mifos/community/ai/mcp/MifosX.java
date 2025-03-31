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
package org.mifos.community.ai.mcp;

import org.mifos.community.ai.mcp.dto.Person;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkus.qute.Qute;
import org.mifos.community.ai.mcp.client.MifosXClient;

public class MifosX {

    @RestClient
    MifosXClient mifosXClient;

    @Tool(description = "Get client details using client account or full name.")
    JsonNode getClientDetails(@ToolArg(description = "Full Client Name (e.g. Jhon Doe)") String clientName) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName; 
        //return formatResults(mifosXClient.getClientDetails(searchParameters));
        return mifosXClient.getClientDetails(searchParameters);
    }
    
    String formatResults(JsonNode results) {    
        Person person = new Person("","","");
        person.setAccount(results.get(0).get("entityAccountNo").asText());
        person.setName(results.get(0).get("entityName").asText());
        person.setPhone(results.get(0).get("entityMobileNo").asText());
        return Qute.fmt(
                        """
                                Name: {p.name} 
                                Account: {p.account} 
                                Phone: {p.phone}
                                """,
                        Map.of("p", person));
    }
   
    public record Result(
            String entityAccountNo,
            String entityMobileNo,
            String entityName,
            Integer entityId) {
    }
    
    public record Results(
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            List<Result>[] clients) {
    }

}
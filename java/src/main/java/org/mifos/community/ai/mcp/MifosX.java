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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import java.util.ArrayList;
import org.mifos.community.ai.mcp.client.MifosXClient;
import org.mifos.community.ai.mcp.dto.LegalForm;

public class MifosX {

    @RestClient
    MifosXClient mifosXClient;
    
    @Tool(description = "Search for a client account by account number or client full name")
    JsonNode searchClientByName(@ToolArg(description = "Full Client Name (e.g. Jhon Doe)") String clientName) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName;
        return mifosXClient.searchClientByName(searchParameters);
    }
    
    @Tool(description = "Get client details by id")
    JsonNode getClientDetailsById(@ToolArg(description = "Client Id (e.g. 1)") Integer clientId) {        
        return mifosXClient.getClientDetailsById(clientId);
    }
    
    /*        
    @Tool(description = "Create a client using client first name, client last name, office id, legal form id and current date.")
    JsonNode createClient(@ToolArg(description = "First Name (e.g. Jhon)") String firstname) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName;
        return mifosXClient.getClientDetails(searchParameters);
    }
    */

}
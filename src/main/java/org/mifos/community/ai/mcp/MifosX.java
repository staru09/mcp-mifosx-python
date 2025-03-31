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

    @Tool(description = "Search for a specific client account and retrieve the details using client account or full name. "
            + "Please ensure that when calling this tool, the input key MUST always be \"input\".")
    JsonNode getClientAccountDetails(@ToolArg(description = "Full Client Name (e.g. Jhon Doe)") String clientName) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName;
        return mifosXClient.getClientDetails(searchParameters);
    }
    
    @Tool(description = "List all the offices. "
            + "Please ensure that when calling this tool, the input key MUST always be \"input\".")
    JsonNode getAllOffices(@ToolArg(description = "Tenant Name (e.g. Mifos)") String tenantName) {        
        return mifosXClient.getOffices();
    }
    
    @Tool(description = "List all the clients")
    JsonNode getAllClients(@ToolArg(description = "Tenant Name (e.g. Mifos)") String tenantName) {        
        return mifosXClient.getClients();
    }
    
    @Tool(description = "Get list of legal forms. "
            + "Please ensure that when calling this tool, the input key MUST always be \"input\".")
    List<LegalForm> getAllLegalForms(@ToolArg(description = "Tenant Name (e.g. Mifos)") String tenantName) {        
        ObjectMapper mapper = new ObjectMapper();
        List<LegalForm> legalForms = new ArrayList<LegalForm>();
        LegalForm lfPerson = new LegalForm();
        lfPerson.setId(1);
        lfPerson.setType("Person");
        legalForms.add(lfPerson);
        LegalForm lfCompany = new LegalForm();
        lfCompany.setId(2);
        lfCompany.setType("Company");
        legalForms.add(lfCompany);
        return legalForms;    
    }
    
    /*
    @Tool(description = "Create a client using client first name, client last name, office id, legal form id and current date.")
    JsonNode createClient(@ToolArg(description = "First Name (e.g. Jhon)") String firstname) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName;
        return mifosXClient.getClientDetails(searchParameters);
    }*/
   
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
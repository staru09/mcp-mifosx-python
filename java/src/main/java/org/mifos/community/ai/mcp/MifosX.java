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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.mifos.community.ai.mcp.client.MifosXClient;
import org.mifos.community.ai.mcp.dto.Client;
import org.mifos.community.ai.mcp.dto.FamilyMember;

public class MifosX {

    @RestClient
    MifosXClient mifosXClient;
    
    @Tool(description = "Search for a client account by account number or client full name")
    JsonNode searchClientByName(@ToolArg(description = "Full Client Name (e.g. Jhon Doe)") String clientName) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientName;
        return mifosXClient.searchClient(searchParameters);
    }
    
    @Tool(description = "Get client by id")
    JsonNode getClientDetailsById(@ToolArg(description = "Client Id (e.g. 1)") Integer clientId) {        
        return mifosXClient.getClientDetailsById(clientId);
    }
    
       
    @Tool(description = "Create a client using first name, last name, email address, mobile number and external id")
    JsonNode createClient(@ToolArg(description = "First Name (e.g. Jhon)", required = true) String firstname, 
            @ToolArg(description = "Last Name (e.g. Doe)", required = true) String lastname,
            @ToolArg(description = "Optional Email Address (e.g. jhon@gmail.com)", required = false) String emailAddress,
            @ToolArg(description = "Optional Mobile Number (e.g. +5215522649494)", required = false) String mobileNo,
            @ToolArg(description = "Optional External Id (e.g. Jhon)", required = false) String externalId) throws JsonProcessingException {
        Client client = new Client();
        client.setFirstname(firstname);
        client.setLastname(lastname);
        client.setEmailAddress(emailAddress);
        client.setMobileNo(mobileNo);
        client.setExternalId(externalId);        
        client.setOfficeId(1);
        client.setLegalFormId(1);
        client.setStaff(false);
        client.setActive(false);        
        client.setDateFormat("yyyy-MM-dd");
        client.setLocale("en");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(client.getDateFormat());
        String formattedDate = currentDate.format(dtf);
        client.setActivationDate(formattedDate);
        client.setSubmittedOnDate(formattedDate);
        ArrayList<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
        client.setFamilyMembers(familyMembers);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonClient = ow.writeValueAsString(client);
        return mifosXClient.createClient(jsonClient);
    }
    

}
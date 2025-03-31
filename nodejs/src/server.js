import { McpServer, ResourceTemplate } from '@modelcontextprotocol/sdk/server/mcp.js';
    import { z } from 'zod';
    import { fineractClient } from './fineract-client.js';

    // Create an MCP server for Apache Fineract
    const server = new McpServer({
      name: "Apache Fineract API",
      version: "1.0.0",
      description: "MCP server for interacting with Apache Fineract API"
    });

    // Add clients resource
    server.resource(
      "clients",
      new ResourceTemplate("fineract://clients/{clientId?}", { list: undefined }),
      async (uri, { clientId }) => {
        try {
          let data;
          let text;
          
          if (clientId) {
            // Get specific client
            data = await fineractClient.getClient(clientId);
            text = `Client ID: ${data.id}\nName: ${data.displayName}\nStatus: ${data.status.value}\nOffice: ${data.officeName}\nActivation Date: ${data.activationDate?.[0]}-${data.activationDate?.[1]}-${data.activationDate?.[2]}\n`;
          } else {
            // List clients
            data = await fineractClient.listClients();
            text = data.map(client => 
              `ID: ${client.id}, Name: ${client.displayName}, Status: ${client.status.value}`
            ).join('\n');
          }
          
          return {
            contents: [{
              uri: uri.href,
              text
            }]
          };
        } catch (error) {
          return {
            contents: [{
              uri: uri.href,
              text: `Error fetching client information: ${error.message}`
            }]
          };
        }
      }
    );

    // Add loans resource
    server.resource(
      "loans",
      new ResourceTemplate("fineract://loans/{loanId?}", { list: undefined }),
      async (uri, { loanId }) => {
        try {
          let data;
          let text;
          
          if (loanId) {
            // Get specific loan
            data = await fineractClient.getLoan(loanId);
            text = `Loan ID: ${data.id}\nClient: ${data.clientName}\nLoan Product: ${data.loanProductName}\nPrincipal: ${data.principal}\nStatus: ${data.status.value}\nDisbursement Date: ${data.timeline?.actualDisbursementDate?.[0]}-${data.timeline?.actualDisbursementDate?.[1]}-${data.timeline?.actualDisbursementDate?.[2]}\n`;
          } else {
            // List loans
            data = await fineractClient.listLoans();
            text = data.map(loan => 
              `ID: ${loan.id}, Client: ${loan.clientName}, Product: ${loan.loanProductName}, Principal: ${loan.principal}, Status: ${loan.status.value}`
            ).join('\n');
          }
          
          return {
            contents: [{
              uri: uri.href,
              text
            }]
          };
        } catch (error) {
          return {
            contents: [{
              uri: uri.href,
              text: `Error fetching loan information: ${error.message}`
            }]
          };
        }
      }
    );

    // Add search clients tool
    server.tool(
      "search_clients",
      { 
        query: z.string().describe("Search query (name, ID, etc.)"),
        limit: z.number().optional().default(10).describe("Maximum number of results to return")
      },
      async ({ query, limit }) => {
        try {
          const clients = await fineractClient.searchClients(query, limit);
          
          if (clients.length === 0) {
            return {
              content: [{ type: "text", text: `No clients found matching "${query}".` }]
            };
          }
          
          const resultText = clients.map(client => 
            `ID: ${client.id}, Name: ${client.displayName}, Status: ${client.status.value}, Office: ${client.officeName}`
          ).join('\n');
          
          return {
            content: [{ 
              type: "text", 
              text: `Found ${clients.length} clients matching "${query}":\n\n${resultText}` 
            }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error searching clients: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Search for clients by name, ID, or other attributes" }
    );

    // Add create client tool
    server.tool(
      "create_client",
      { 
        firstName: z.string().describe("Client's first name"),
        lastName: z.string().describe("Client's last name"),
        officeId: z.number().describe("ID of the office"),
        dateOfBirth: z.string().optional().describe("Date of birth in dd MMMM yyyy format"),
        mobileNo: z.string().optional().describe("Mobile phone number")
      },
      async ({ firstName, lastName, officeId, dateOfBirth, mobileNo }) => {
        try {
          const clientData = {
            firstname: firstName,
            lastname: lastName,
            officeId: officeId,
            active: false,
            legalFormId: 1,
            dateOfBirth: dateOfBirth,
            locale: "en",
            dateFormat: "dd MMMM yyyy"
          };
          
          if (mobileNo) {
            clientData.mobileNo = mobileNo;
          }
          
          const result = await fineractClient.createClient(clientData);
          
          return {
            content: [{ 
              type: "text", 
              text: `Client created successfully!\nClient ID: ${result.clientId}\nResource ID: ${result.resourceId}\nRequest your supervisor to activate the client` 
            }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error creating client: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Create a new client in the system" }
    );

    // Add get loan details tool
    server.tool(
      "get_loan_details",
      { 
        loanId: z.number().describe("ID of the loan to retrieve")
      },
      async ({ loanId }) => {
        try {
          const loan = await fineractClient.getLoan(loanId);
          
          const loanDetails = `
Loan Details:
ID: ${loan.id}
Account Number: ${loan.accountNo}
Client: ${loan.clientName} (ID: ${loan.clientId})
Loan Product: ${loan.loanProductName}
Principal: ${loan.principal} ${loan.currency.code}
Interest Rate: ${loan.interestRatePerPeriod}% per ${loan.interestRatePeriodFrequencyType.value}
Term: ${loan.numberOfRepayments} ${loan.repaymentFrequencyType.value}
Status: ${loan.status.value}
Disbursement Date: ${loan.timeline?.actualDisbursementDate ? loan.timeline.actualDisbursementDate.join('-') : 'Not disbursed'}
          `;
          
          return {
            content: [{ type: "text", text: loanDetails.trim() }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error retrieving loan details: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Get detailed information about a specific loan" }
    );

    // Add create loan application tool
    server.tool(
      "create_loan_application",
      { 
        clientId: z.number().describe("ID of the client"),
        productId: z.number().describe("ID of the loan product"),
        principal: z.number().describe("Principal amount"),
        loanTermFrequency: z.number().describe("Loan term frequency"),
        loanTermFrequencyType: z.number().describe("Loan term frequency type (e.g., 2 for months)"),
        numberOfRepayments: z.number().describe("Number of repayments"),
        repaymentEvery: z.number().describe("Repayment frequency"),
        repaymentFrequencyType: z.number().describe("Repayment frequency type (e.g., 2 for months)"),
        interestRatePerPeriod: z.number().describe("Interest rate per period"),
        expectedDisbursementDate: z.string().describe("Expected disbursement date (YYYY-MM-DD)")
      },
      async ({ clientId, productId, principal, loanTermFrequency, loanTermFrequencyType, 
               numberOfRepayments, repaymentEvery, repaymentFrequencyType, 
               interestRatePerPeriod, expectedDisbursementDate }) => {
        try {
          const [year, month, day] = expectedDisbursementDate.split('-').map(Number);
          
          const loanData = {
            clientId,
            productId,
            principal,
            loanTermFrequency,
            loanTermFrequencyType,
            numberOfRepayments,
            repaymentEvery,
            repaymentFrequencyType,
            interestRatePerPeriod,
            amortizationType: 1, // Equal installments
            interestType: 1, // Declining balance
            interestCalculationPeriodType: 1, // Daily
            transactionProcessingStrategyId: 1, // Mifos strategy
            expectedDisbursementDate: [year, month, day],
            submittedOnDate: [year, month, day]
          };
          
          const result = await fineractClient.createLoanApplication(loanData);
          
          return {
            content: [{ 
              type: "text", 
              text: `Loan application created successfully!\nLoan ID: ${result.loanId}\nResource ID: ${result.resourceId}` 
            }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error creating loan application: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Create a new loan application" }
    );

    // Add approve loan tool
    server.tool(
      "approve_loan",
      { 
        loanId: z.number().describe("ID of the loan to approve"),
        approvedOnDate: z.string().describe("Approval date (YYYY-MM-DD)")
      },
      async ({ loanId, approvedOnDate }) => {
        try {
          const [year, month, day] = approvedOnDate.split('-').map(Number);
          
          const approvalData = {
            approvedOnDate: [year, month, day]
          };
          
          const result = await fineractClient.approveLoan(loanId, approvalData);
          
          return {
            content: [{ 
              type: "text", 
              text: `Loan approved successfully!\nLoan ID: ${loanId}\nChanges applied: ${result.changes ? JSON.stringify(result.changes) : 'None'}` 
            }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error approving loan: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Approve a pending loan application" }
    );

    // Add disburse loan tool
    server.tool(
      "disburse_loan",
      { 
        loanId: z.number().describe("ID of the loan to disburse"),
        disbursementDate: z.string().describe("Disbursement date (YYYY-MM-DD)")
      },
      async ({ loanId, disbursementDate }) => {
        try {
          const [year, month, day] = disbursementDate.split('-').map(Number);
          
          const disbursementData = {
            actualDisbursementDate: [year, month, day],
            transactionAmount: 0 // Will use the approved amount
          };
          
          const result = await fineractClient.disburseLoan(loanId, disbursementData);
          
          return {
            content: [{ 
              type: "text", 
              text: `Loan disbursed successfully!\nLoan ID: ${loanId}\nChanges applied: ${result.changes ? JSON.stringify(result.changes) : 'None'}` 
            }]
          };
        } catch (error) {
          return {
            content: [{ type: "text", text: `Error disbursing loan: ${error.message}` }],
            isError: true
          };
        }
      },
      { description: "Disburse an approved loan" }
    );

    export { server };

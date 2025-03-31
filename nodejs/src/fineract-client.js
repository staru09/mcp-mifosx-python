import axios from 'axios';
    import dotenv from 'dotenv';
    import { Buffer } from 'buffer';

    // Load environment variables
    dotenv.config();

    const BASE_URL = process.env.FINERACT_BASE_URL || 'https://sandbox.mifos.community/fineract-provider/api/v1';
    const USERNAME = process.env.FINERACT_USERNAME || 'mifos';
    const PASSWORD = process.env.FINERACT_PASSWORD || 'password';
    const TENANT_ID = process.env.FINERACT_TENANT_ID || 'default';

    // Create axios instance with authentication
    const api = axios.create({
      baseURL: BASE_URL,
      headers: {
        'Content-Type': 'application/json',
        'Fineract-Platform-TenantId': TENANT_ID,
        'Authorization': `Basic ${Buffer.from(`${USERNAME}:${PASSWORD}`).toString('base64')}`
      }
    });

    // Handle API errors
    api.interceptors.response.use(
      response => response,
      error => {
        console.error('API Error:', error.response?.data || error.message);
        
        // Format error message
        let errorMessage = 'API request failed';
        if (error.response?.data?.defaultUserMessage) {
          errorMessage = error.response.data.defaultUserMessage;
        } else if (error.response?.data?.errors && error.response.data.errors.length > 0) {
          errorMessage = error.response.data.errors.map(e => e.defaultUserMessage || e.developerMessage).join(', ');
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        const enhancedError = new Error(errorMessage);
        enhancedError.statusCode = error.response?.status;
        enhancedError.details = error.response?.data;
        
        throw enhancedError;
      }
    );

    // Fineract API client
    export const fineractClient = {
      // Client endpoints
      async listClients(offset = 0, limit = 20) {
        const response = await api.get('/clients', {
          params: { offset, limit }
        });
        return response.data;
      },
      
      async getClient(clientId) {
        const response = await api.get(`/clients/${clientId}`);
        return response.data;
      },
      
      async searchClients(query, limit = 10) {
        const response = await api.get('/search', {
          params: { query, resource: 'clients', exactMatch: 'false', limit }
        });
        return response.data;
      },
      
      async createClient(clientData) {
        const response = await api.post('/clients', clientData);
        return response.data;
      },
      
      async activateClient(clientId, activationData) {
        const response = await api.post(`/clients/${clientId}?command=activate`, activationData);
        return response.data;
      },
      
      // Loan endpoints
      async listLoans(offset = 0, limit = 20) {
        const response = await api.get('/loans', {
          params: { offset, limit }
        });
        return response.data;
      },
      
      async getLoan(loanId) {
        const response = await api.get(`/loans/${loanId}`);
        return response.data;
      },
      
      async createLoanApplication(loanData) {
        const response = await api.post('/loans', loanData);
        return response.data;
      },
      
      async approveLoan(loanId, approvalData) {
        const response = await api.post(`/loans/${loanId}?command=approve`, approvalData);
        return response.data;
      },
      
      async disburseLoan(loanId, disbursementData) {
        const response = await api.post(`/loans/${loanId}?command=disburse`, disbursementData);
        return response.data;
      },
      
      async getLoanRepaymentSchedule(loanId) {
        const response = await api.get(`/loans/${loanId}/repaymentschedule`);
        return response.data;
      },
      
      async makeLoanRepayment(loanId, repaymentData) {
        const response = await api.post(`/loans/${loanId}/transactions?command=repayment`, repaymentData);
        return response.data;
      },
      
      // Loan product endpoints
      async listLoanProducts() {
        const response = await api.get('/loanproducts');
        return response.data;
      },
      
      async getLoanProduct(productId) {
        const response = await api.get(`/loanproducts/${productId}`);
        return response.data;
      }
    };

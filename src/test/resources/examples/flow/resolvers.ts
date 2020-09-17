import inputMapper from "../graphql/inputMapper";

export default {
  Mutation: {
    createOrganization: (_: any, { body }: { body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post("/organizations", inputMapper("OrganizationFormInput", body)),

    updateOrganization: (_: any, { organizationId, body }: { organizationId: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/organizations/${organizationId}`, inputMapper("OrganizationPutFormInput", body)),

    deleteOrganization: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.delete(`/organizations/${organizationId}`, {}),

    updateLine: (_: any, { id, body }: { id: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/lines/${id}`, inputMapper("CheckoutLineFormInput", body)),

    deleteLine: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.delete(`/lines/${id}`, {}),

    createCheckoutBySessionId: (_: any, { sessionId }: { sessionId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post(`/v2/checkouts/session/${sessionId}`, {}),

    createCheckoutLine: (_: any, { id, body }: { id: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post(`/v2/checkouts/${id}/lines`, inputMapper("CheckoutLineFormInput", body)),

    upsertCheckoutLines: (_: any, { id, body }: { id: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/v2/checkouts/${id}/lines`, inputMapper("CheckoutLinesFormInput", body))
  },

  ConsumerInvoiceLineDiscriminator: {
    ITEM: "item",
    DISCOUNT: "discount",
    SHIPPING: "shipping"
  },

  Query: {
    b2bCreditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    b2bInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    consumerInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/consumer/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    creditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    organizations: (_: any, { id, name, environment, parent, limit, offset, sort }: { id: any, name: string, environment: any, parent: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get("/organizations", { id, name, environment: inputMapper("Environment", environment), parent, limit, offset, sort }),

    organization: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/organizations/${organizationId}`),

    publicKey: (_: any, { organization }: { organization: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/encryption/keys/latest`),

    line: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/lines/${id}`),

    checkout: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/v2/checkouts/${id}`)
  },

  Environment: {
    SANDBOX: "sandbox",
    PRODUCTION: "production"
  },

  ConsumerInvoiceDocumentType: {
    PDF: "pdf"
  },

  ConsumerInvoiceCustomerType: {
    BUSINESS_EU_VERIFIED: "business_eu_verified",
    BUSINESS_NON_VERIFIED: "business_non_verified",
    INDIVIDUAL: "individual"
  },

  ConsumerInvoiceStatus: {
    PENDING: "pending",
    AVAILABLE: "available",
    INVALID: "invalid"
  },

  TaxVerificationResult: {
    VALID: "valid",
    INVALID: "invalid",
    UNABLE_TO_VALIDATE: "unable_to_validate"
  },

  EconomicTitleLocation: {
    HIGH_SEAS: "high_seas",
    ORIGINATION: "origination",
    DESTINATION: "destination"
  },

  CheckoutErrorCode: {
    GENERIC_ERROR: "generic_error"
  },

  CheckoutRedirectMethod: {
    GET: "get",
    POST: "post"
  },

  ConsumerInvoiceLine: {
    __resolveType(obj: any, _: any, __: any) {
      switch (obj.discriminator) {
        case "consumer_invoice_line_item":
          return "ConsumerInvoiceLineItem";
        case "item":
          return "ConsumerInvoiceLineItem";
        case "consumer_invoice_line_discount":
          return "ConsumerInvoiceLineDiscount";
        case "discount":
          return "ConsumerInvoiceLineDiscount";
        case "consumer_invoice_line_shipping":
          return "ConsumerInvoiceLineShipping";
        case "shipping":
          return "ConsumerInvoiceLineShipping";
      }
      throw `Unable to resolve discriminator '${obj.discriminator}' for union 'ConsumerInvoiceLine'`;
    }
  }
}
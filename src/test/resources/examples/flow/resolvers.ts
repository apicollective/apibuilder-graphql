import inputMapper from "../graphql/inputMapper";

export default {
  Query: {
    b2bCreditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    b2bInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    consumerInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/consumer/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    creditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    orderSummary: (_: any, { organization, number }: { organization: string, number: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/order/summaries/${number}`),

    organizations: (_: any, { id, channelId, name, environment, parent, inChannel, limit, offset, sort }: { id: any, channelId: string, name: string, environment: any, parent: string, inChannel: boolean, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get("/organizations", { id, channel_id: channelId, name, environment: inputMapper("Environment", environment), parent, in_channel: inChannel, limit, offset, sort }),

    organization: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/organizations/${organizationId}`),

    publicKey: (_: any, { organization }: { organization: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/encryption/keys/latest`)
  },

  Mutation: {
    organization: () => ({}) // OrganizationMutations
  },

  B2bInvoiceType: {
    SELF_BILL_INVOICE: "self_bill_invoice",
    INVOICE: "invoice"
  },

  ConsumerInvoiceCustomerType: {
    BUSINESS_EU_VERIFIED: "business_eu_verified",
    BUSINESS_NON_VERIFIED: "business_non_verified",
    INDIVIDUAL: "individual"
  },

  ConsumerInvoiceDocumentType: {
    PDF: "pdf"
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
  },

  ConsumerInvoiceLineDiscriminator: {
    ITEM: "item",
    DISCOUNT: "discount",
    SHIPPING: "shipping"
  },

  ConsumerInvoiceStatus: {
    PENDING: "pending",
    AVAILABLE: "available",
    INVALID: "invalid"
  },

  EconomicTitleLocation: {
    HIGH_SEAS: "high_seas",
    ORIGINATION: "origination",
    DESTINATION: "destination"
  },

  EntityIdentifierType: {
    IOSS: "ioss",
    VOEC: "voec",
    ZAZ: "zaz"
  },

  Environment: {
    SANDBOX: "sandbox",
    PRODUCTION: "production"
  },

  OrganizationMutations: {
    create: (_: any, { body }: { body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post("/organizations", inputMapper("OrganizationFormInput", body)),

    update: (_: any, { organizationId, body }: { organizationId: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/organizations/${organizationId}`, inputMapper("OrganizationPutFormInput", body)),

    delete: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.delete(`/organizations/${organizationId}`, {})
  },

  OrganizationStatus: {
    ACTIVE: "active",
    INACTIVE: "inactive",
    DEACTIVATED: "deactivated",
    PROVISIONED: "provisioned"
  },

  OrganizationType: {
    STANDALONE: "standalone",
    CHANNEL: "channel"
  },

  TaxVerificationResult: {
    VALID: "valid",
    INVALID: "invalid",
    UNABLE_TO_VALIDATE: "unable_to_validate"
  }
}
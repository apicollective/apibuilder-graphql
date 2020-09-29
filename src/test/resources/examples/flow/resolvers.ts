import inputMapper from "../graphql/inputMapper";

export default {
  Query: {
    b2bCreditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    b2bInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/b2b/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    checkout: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/v2/checkouts/${id}`),

    checkoutLine: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/lines/${id}`),

    consumerInvoices: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/consumer/invoices`, { id, key, order_number: orderNumber, limit, offset, sort }),

    creditMemos: (_: any, { organization, id, key, orderNumber, limit, offset, sort }: { organization: string, id: any, key: string, orderNumber: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/credit/memos`, { id, key, order_number: orderNumber, limit, offset, sort }),

    organizations: (_: any, { id, name, environment, parent, limit, offset, sort }: { id: any, name: string, environment: any, parent: string, limit: number, offset: number, sort: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get("/organizations", { id, name, environment: inputMapper("Environment", environment), parent, limit, offset, sort }),

    organization: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/organizations/${organizationId}`),

    publicKey: (_: any, { organization }: { organization: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.get(`/${organization}/encryption/keys/latest`)
  },

  Mutation: {
    checkout: () => ({}), // CheckoutMutations
    checkoutLine: () => ({}), // CheckoutLineMutations
    checkoutModification: () => ({}), // CheckoutModificationMutations
    organization: () => ({}) // OrganizationMutations
  },

  CheckoutLineMutations: {
    createLine: (_: any, { body }: { body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post("/lines", inputMapper("CheckoutLineFormInput", body)),

    updateLineQuantity: (_: any, { id, body }: { id: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/lines/${id}/quantity`, inputMapper("CheckoutLineQuantityFormInput", body)),

    deleteLine: (_: any, { id }: { id: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.delete(`/lines/${id}`, {})
  },

  CheckoutModificationForm: {
    __resolveType(obj: any, _: any, __: any) {
      switch (obj.discriminator) {
        case "checkout_modification_attributes_form":
          return "CheckoutModificationAttributesForm";
        case "checkout_modification_currency_form":
          return "CheckoutModificationCurrencyForm";
        case "checkout_modification_destination_address_streets_form":
          return "CheckoutModificationDestinationAddressStreetsForm";
        case "checkout_modification_destination_address_city_form":
          return "CheckoutModificationDestinationAddressCityForm";
        case "checkout_modification_destination_address_province_form":
          return "CheckoutModificationDestinationAddressProvinceForm";
        case "checkout_modification_destination_address_postal_form":
          return "CheckoutModificationDestinationAddressPostalForm";
        case "checkout_modification_destination_address_country_form":
          return "CheckoutModificationDestinationAddressCountryForm";
        case "checkout_modification_language_form":
          return "CheckoutModificationLanguageForm";
        case "checkout_modification_line_form":
          return "CheckoutModificationLineForm";
        case "checkout_line_modification_delete_form":
          return "CheckoutLineModificationDeleteForm";
        case "checkout_line_modification_quantity_form":
          return "CheckoutLineModificationQuantityForm";
        case "checkout_modification_promotion_code_form":
          return "CheckoutModificationPromotionCodeForm";
      }
      throw `Unable to resolve discriminator '${obj.discriminator}' for union 'CheckoutModificationForm'`;
    }
  },

  CheckoutModificationFormDiscriminator: {
    CHECKOUT_MODIFICATION_ATTRIBUTES_FORM: "checkout_modification_attributes_form",
    CHECKOUT_MODIFICATION_CURRENCY_FORM: "checkout_modification_currency_form",
    CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_STREETS_FORM: "checkout_modification_destination_address_streets_form",
    CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_CITY_FORM: "checkout_modification_destination_address_city_form",
    CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_PROVINCE_FORM: "checkout_modification_destination_address_province_form",
    CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_POSTAL_FORM: "checkout_modification_destination_address_postal_form",
    CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_COUNTRY_FORM: "checkout_modification_destination_address_country_form",
    CHECKOUT_MODIFICATION_LANGUAGE_FORM: "checkout_modification_language_form",
    CHECKOUT_MODIFICATION_LINE_FORM: "checkout_modification_line_form",
    CHECKOUT_LINE_MODIFICATION_DELETE_FORM: "checkout_line_modification_delete_form",
    CHECKOUT_LINE_MODIFICATION_QUANTITY_FORM: "checkout_line_modification_quantity_form",
    CHECKOUT_MODIFICATION_PROMOTION_CODE_FORM: "checkout_modification_promotion_code_form"
  },

  CheckoutModificationMutations: {
    checkoutModification: (_: any, { checkoutId, body }: { checkoutId: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post(`/v2/checkouts/${checkoutId}/modifications`, inputMapper("CheckoutModificationFormInput", body))
  },

  CheckoutMutations: {
    createCheckoutBySessionId: (_: any, { sessionId }: { sessionId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post(`/v2/checkouts/session/${sessionId}`, {})
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

  Environment: {
    SANDBOX: "sandbox",
    PRODUCTION: "production"
  },

  OrganizationMutations: {
    createOrganization: (_: any, { body }: { body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.post("/organizations", inputMapper("OrganizationFormInput", body)),

    updateOrganization: (_: any, { organizationId, body }: { organizationId: string, body: any }, { dataSources }: { dataSources: any }) =>
      dataSources.api.put(`/organizations/${organizationId}`, inputMapper("OrganizationPutFormInput", body)),

    deleteOrganization: (_: any, { organizationId }: { organizationId: string }, { dataSources }: { dataSources: any }) =>
      dataSources.api.delete(`/organizations/${organizationId}`, {})
  },

  TaxVerificationResult: {
    VALID: "valid",
    INVALID: "invalid",
    UNABLE_TO_VALIDATE: "unable_to_validate"
  }
}
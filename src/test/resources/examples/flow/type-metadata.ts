
class TypeData {
  constructor(readonly name: string, readonly kind: string) {}
}

class EnumValue {
  constructor(readonly graphQLName: string, readonly serverName: string) {}
}

export class EnumData {
  private readonly byGraphQLName: {
    [_: string]: EnumValue;
  } = {};

  constructor(readonly name: string, readonly values: EnumValue[]) {
    values.forEach((f) => (this.byGraphQLName[f.graphQLName] = f));
  }

  getEnumValue(graphQLName: string): EnumValue | null {
    return this.byGraphQLName[graphQLName];
  }
}

class FieldData {
  constructor(
    readonly graphQLName: string,
    readonly serverName: string,
    readonly type: TypeData
  ) {}
}

export class ModelData {
  private readonly byGraphQLName: {
    [_: string]: FieldData;
  } = {};

  constructor(readonly name: string, readonly fields: FieldData[]) {
    fields.forEach((f) => (this.byGraphQLName[f.graphQLName] = f));
  }

  getField(name: string): FieldData | null {
    return this.byGraphQLName[name];
  }
}

type AnyTypeData = ModelData | EnumData;

class Models {
  private readonly byGraphQLName: {
    [_: string]: AnyTypeData;
  } = {};

  constructor(readonly models: AnyTypeData[]) {
    models.forEach((f) => (this.byGraphQLName[f.name] = f));
  }

  getTypeData(name: string): AnyTypeData | null {
    return this.byGraphQLName[name];
  }

  getModelField(name: string, fieldName: string): FieldData | null {
    const m = this.getTypeData(name);
    if (m) {
      if (m instanceof ModelData) {
        return m.getField(fieldName);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
}

const field = (
  graphQLName: string,
  serverName: string,
  typeName: string,
  typeKind: string
) => new FieldData(graphQLName, serverName, new TypeData(typeName, typeKind));


export const models = new Models([
  new EnumData("Environment", [
    new EnumValue("SANDBOX", "sandbox"),
    new EnumValue("PRODUCTION", "production")
  ]),
  new EnumData("ConsumerInvoiceDocumentType", [
    new EnumValue("PDF", "pdf")
  ]),
  new EnumData("ConsumerInvoiceStatus", [
    new EnumValue("PENDING", "pending"),
    new EnumValue("AVAILABLE", "available"),
    new EnumValue("INVALID", "invalid")
  ]),
  new EnumData("ConsumerInvoiceCustomerType", [
    new EnumValue("BUSINESS_EU_VERIFIED", "business_eu_verified"),
    new EnumValue("BUSINESS_NON_VERIFIED", "business_non_verified"),
    new EnumValue("INDIVIDUAL", "individual")
  ]),
  new EnumData("TaxVerificationResult", [
    new EnumValue("VALID", "valid"),
    new EnumValue("INVALID", "invalid"),
    new EnumValue("UNABLE_TO_VALIDATE", "unable_to_validate")
  ]),
  new EnumData("EconomicTitleLocation", [
    new EnumValue("HIGH_SEAS", "high_seas"),
    new EnumValue("ORIGINATION", "origination"),
    new EnumValue("DESTINATION", "destination")
  ]),
  new EnumData("CheckoutErrorCode", [
    new EnumValue("GENERIC_ERROR", "generic_error")
  ]),
  new EnumData("CheckoutRedirectMethod", [
    new EnumValue("GET", "get"),
    new EnumValue("POST", "post")
  ]),
  new ModelData("Address", [
    field("text", "text", "String", "scalar"),
    field("streets", "streets", "String", "array"),
    field("streetNumber", "street_number", "String", "scalar"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("latitude", "latitude", "String", "scalar"),
    field("longitude", "longitude", "String", "scalar")
  ]),
  new ModelData("Contact", [
    field("name", "name", "Name", "type"),
    field("company", "company", "String", "scalar"),
    field("email", "email", "String", "scalar"),
    field("phone", "phone", "String", "scalar")
  ]),
  new ModelData("Price", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar"),
    field("label", "label", "String", "scalar")
  ]),
  new ModelData("OrganizationReference", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("BillingAddress", [
    field("name", "name", "Name", "type"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("company", "company", "String", "scalar")
  ]),
  new ModelData("Name", [
    field("first", "first", "String", "scalar"),
    field("last", "last", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntityRegistration", [
    field("number", "number", "String", "scalar"),
    field("country", "country", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntity", [
    field("organization", "organization", "OrganizationReference", "type"),
    field("name", "name", "String", "scalar"),
    field("vat", "vat", "MerchantOfRecordEntityRegistration", "type"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("phone", "phone", "String", "scalar"),
    field("email", "email", "String", "scalar")
  ]),
  new ModelData("OrganizationDefaults", [
    field("country", "country", "String", "scalar"),
    field("baseCurrency", "base_currency", "String", "scalar"),
    field("language", "language", "String", "scalar"),
    field("locale", "locale", "String", "scalar"),
    field("timezone", "timezone", "String", "scalar")
  ]),
  new ModelData("ItemReference", [
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("Money", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("OrganizationSummary", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar")
  ]),
  new ModelData("Organization", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parent", "parent", "OrganizationReference", "type"),
    field("defaults", "defaults", "OrganizationDefaults", "type"),
    field("createdAt", "created_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("ConsumerInvoiceCenterReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "Address", "type")
  ]),
  new ModelData("ConsumerInvoiceLevy", [
    field("rate", "rate", "Decimal", "scalar"),
    field("value", "value", "Price", "type")
  ]),
  new ModelData("ConsumerInvoiceOrderSummary", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("submittedAt", "submitted_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("B2bInvoice", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("buyer", "buyer", "MerchantOfRecordEntity", "type"),
    field("seller", "seller", "MerchantOfRecordEntity", "type"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("order", "order", "ConsumerInvoiceOrderSummary", "type"),
    field("economicTitleLocation", "economic_title_location", "EconomicTitleLocation", "enum"),
    field("center", "center", "ConsumerInvoiceCenterReference", "type"),
    field("destination", "destination", "OrderAddress", "type"),
    field("tax", "tax", "Money", "type"),
    field("lines", "lines", "ConsumerInvoiceLine", "array"),
    field("documents", "documents", "ConsumerInvoiceDocument", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("estimatedDeliveryDate", "estimated_delivery_date", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("ConsumerInvoiceLineShipping", [
    field("price", "price", "Price", "type"),
    field("discount", "discount", "Price", "type"),
    field("tax", "tax", "ConsumerInvoiceLevy", "type"),
    field("duty", "duty", "ConsumerInvoiceLevy", "type")
  ]),
  new ModelData("ConsumerInvoiceLineItem", [
    field("item", "item", "ItemReference", "type"),
    field("description", "description", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("unitPrice", "unit_price", "Price", "type"),
    field("unitDiscount", "unit_discount", "Price", "type"),
    field("unitTax", "unit_tax", "ConsumerInvoiceLevy", "type"),
    field("unitDuty", "unit_duty", "ConsumerInvoiceLevy", "type")
  ]),
  new ModelData("ConsumerInvoiceLineDiscount", [
    field("price", "price", "Price", "type")
  ]),
  new ModelData("ConsumerInvoiceReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoicePayment", [
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("description", "description", "String", "scalar"),
    field("value", "value", "Price", "type"),
    field("billingAddress", "billing_address", "BillingAddress", "type")
  ]),
  new ModelData("ConsumerInvoiceDocument", [
    field("type", "type", "ConsumerInvoiceDocumentType", "enum"),
    field("language", "language", "String", "scalar"),
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoice", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("order", "order", "ConsumerInvoiceOrderSummary", "type"),
    field("entity", "entity", "MerchantOfRecordEntity", "type"),
    field("payments", "payments", "ConsumerInvoicePayment", "array"),
    field("center", "center", "ConsumerInvoiceCenterReference", "type"),
    field("destination", "destination", "OrderAddress", "type"),
    field("billingAddress", "billing_address", "BillingAddress", "type"),
    field("lines", "lines", "ConsumerInvoiceLine", "array"),
    field("documents", "documents", "ConsumerInvoiceDocument", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("taxRegistration", "tax_registration", "TaxRegistration", "type"),
    field("customerType", "customer_type", "ConsumerInvoiceCustomerType", "enum"),
    field("estimatedDeliveryDate", "estimated_delivery_date", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("B2bInvoiceReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("CreditMemo", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("invoice", "invoice", "ConsumerInvoiceReference", "type"),
    field("entity", "entity", "MerchantOfRecordEntity", "type"),
    field("payments", "payments", "ConsumerInvoicePayment", "array"),
    field("lines", "lines", "ConsumerInvoiceLine", "array"),
    field("documents", "documents", "ConsumerInvoiceDocument", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("taxRegistration", "tax_registration", "TaxRegistration", "type")
  ]),
  new ModelData("B2bCreditMemo", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("buyer", "buyer", "MerchantOfRecordEntity", "type"),
    field("seller", "seller", "MerchantOfRecordEntity", "type"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("invoice", "invoice", "B2bInvoiceReference", "type"),
    field("lines", "lines", "ConsumerInvoiceLine", "array"),
    field("documents", "documents", "ConsumerInvoiceDocument", "array"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("OrderAddress", [
    field("text", "text", "String", "scalar"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("latitude", "latitude", "String", "scalar"),
    field("longitude", "longitude", "String", "scalar"),
    field("contact", "contact", "Contact", "type")
  ]),
  new ModelData("OrderSummaryImage", [
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("OrderSummaryLineItem", [
    field("item", "item", "OrderSummaryItem", "type"),
    field("quantity", "quantity", "Long", "scalar"),
    field("discount", "discount", "Price", "type"),
    field("tax", "tax", "OrderSummaryLevy", "type"),
    field("duty", "duty", "OrderSummaryLevy", "type"),
    field("total", "total", "Price", "type"),
    field("priceAttributes", "price_attributes", "Object", "scalar")
  ]),
  new ModelData("OrderSummaryPriceDetail", [
    field("price", "price", "Price", "type"),
    field("name", "name", "String", "scalar"),
    field("rate", "rate", "Decimal", "scalar"),
    field("rateLabel", "rate_label", "String", "scalar")
  ]),
  new ModelData("OrderSummaryItem", [
    field("number", "number", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("description", "description", "String", "scalar"),
    field("attributes", "attributes", "CheckoutItemContentAttribute", "array"),
    field("image", "image", "OrderSummaryImage", "type"),
    field("price", "price", "Price", "type"),
    field("discount", "discount", "Price", "type"),
    field("tax", "tax", "OrderSummaryLevy", "type"),
    field("duty", "duty", "OrderSummaryLevy", "type"),
    field("priceAttributes", "price_attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutItemContentAttribute", [
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("value", "value", "String", "scalar")
  ]),
  new ModelData("OrderSummaryLevy", [
    field("rate", "rate", "Decimal", "scalar"),
    field("rateLabel", "rate_label", "String", "scalar"),
    field("value", "value", "Price", "type")
  ]),
  new ModelData("OrderSummary", [
    field("number", "number", "String", "scalar"),
    field("subtotal", "subtotal", "OrderSummaryPriceDetail", "type"),
    field("shipping", "shipping", "OrderSummaryPriceDetail", "type"),
    field("tax", "tax", "OrderSummaryPriceDetail", "type"),
    field("duty", "duty", "OrderSummaryPriceDetail", "type"),
    field("insurance", "insurance", "OrderSummaryPriceDetail", "type"),
    field("discount", "discount", "OrderSummaryPriceDetail", "type"),
    field("surcharges", "surcharges", "OrderSummaryPriceDetail", "type"),
    field("adjustment", "adjustment", "OrderSummaryPriceDetail", "type"),
    field("total", "total", "OrderSummaryPriceDetail", "type"),
    field("lines", "lines", "OrderSummaryLineItem", "array"),
    field("identifiers", "identifiers", "Object", "scalar")
  ]),
  new ModelData("TaxRegistration", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("timestamp", "timestamp", "DateTimeIso8601", "scalar"),
    field("result", "result", "TaxVerificationResult", "enum"),
    field("resultReason", "result_reason", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "String", "scalar"),
    field("companyName", "company_name", "String", "scalar")
  ]),
  new ModelData("OrganizationPutForm", [
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaults", "type")
  ]),
  new ModelData("OrganizationForm", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaults", "type")
  ]),
  new ModelData("PublicKey", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutError", [
    field("code", "code", "CheckoutErrorCode", "enum"),
    field("messages", "messages", "String", "array"),
    field("redirect", "redirect", "CheckoutRedirect", "type")
  ]),
  new ModelData("CheckoutReference", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutLine", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReference", "type"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutLineForm", [
    field("checkoutId", "checkout_id", "String", "scalar"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("Checkout", [
    field("id", "id", "String", "scalar"),
    field("organization", "organization", "OrganizationSummary", "type"),
    field("order", "order", "OrderSummary", "type"),
    field("errors", "errors", "CheckoutError", "array")
  ]),
  new ModelData("CheckoutRedirect", [
    field("method", "method", "CheckoutRedirectMethod", "enum"),
    field("url", "url", "String", "scalar"),
    field("body", "body", "String", "scalar")
  ]),
  new EnumData("ConsumerInvoiceLineDiscriminator", [
    new EnumValue("ITEM", "item"),
    new EnumValue("DISCOUNT", "discount"),
    new EnumValue("SHIPPING", "shipping")
  ]),
  new ModelData("AddressInput", [
    field("text", "text", "String", "scalar"),
    field("streets", "streets", "String", "array"),
    field("streetNumber", "street_number", "String", "scalar"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("latitude", "latitude", "String", "scalar"),
    field("longitude", "longitude", "String", "scalar")
  ]),
  new ModelData("ContactInput", [
    field("name", "name", "NameInput", "input"),
    field("company", "company", "String", "scalar"),
    field("email", "email", "String", "scalar"),
    field("phone", "phone", "String", "scalar")
  ]),
  new ModelData("PriceInput", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar"),
    field("label", "label", "String", "scalar")
  ]),
  new ModelData("OrganizationReferenceInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("BillingAddressInput", [
    field("name", "name", "NameInput", "input"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("company", "company", "String", "scalar")
  ]),
  new ModelData("NameInput", [
    field("first", "first", "String", "scalar"),
    field("last", "last", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntityRegistrationInput", [
    field("number", "number", "String", "scalar"),
    field("country", "country", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntityInput", [
    field("organization", "organization", "OrganizationReferenceInput", "input"),
    field("name", "name", "String", "scalar"),
    field("vat", "vat", "MerchantOfRecordEntityRegistrationInput", "input"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("phone", "phone", "String", "scalar"),
    field("email", "email", "String", "scalar")
  ]),
  new ModelData("OrganizationDefaultsInput", [
    field("country", "country", "String", "scalar"),
    field("baseCurrency", "base_currency", "String", "scalar"),
    field("language", "language", "String", "scalar"),
    field("locale", "locale", "String", "scalar"),
    field("timezone", "timezone", "String", "scalar")
  ]),
  new ModelData("ItemReferenceInput", [
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("MoneyInput", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("OrganizationSummaryInput", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar")
  ]),
  new ModelData("OrganizationInput", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parent", "parent", "OrganizationReferenceInput", "input"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input"),
    field("createdAt", "created_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("ConsumerInvoiceCenterReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "AddressInput", "input")
  ]),
  new ModelData("ConsumerInvoiceLevyInput", [
    field("rate", "rate", "Decimal", "scalar"),
    field("value", "value", "PriceInput", "input")
  ]),
  new ModelData("ConsumerInvoiceOrderSummaryInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("submittedAt", "submitted_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("B2bInvoiceInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("buyer", "buyer", "MerchantOfRecordEntityInput", "input"),
    field("seller", "seller", "MerchantOfRecordEntityInput", "input"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("order", "order", "ConsumerInvoiceOrderSummaryInput", "input"),
    field("economicTitleLocation", "economic_title_location", "EconomicTitleLocation", "enum"),
    field("center", "center", "ConsumerInvoiceCenterReferenceInput", "input"),
    field("destination", "destination", "OrderAddressInput", "input"),
    field("tax", "tax", "MoneyInput", "input"),
    field("lines", "lines", "ConsumerInvoiceLineInput", "array"),
    field("documents", "documents", "ConsumerInvoiceDocumentInput", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("estimatedDeliveryDate", "estimated_delivery_date", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("ConsumerInvoiceLineShippingInput", [
    field("price", "price", "PriceInput", "input"),
    field("discount", "discount", "PriceInput", "input"),
    field("tax", "tax", "ConsumerInvoiceLevyInput", "input"),
    field("duty", "duty", "ConsumerInvoiceLevyInput", "input")
  ]),
  new ModelData("ConsumerInvoiceLineItemInput", [
    field("item", "item", "ItemReferenceInput", "input"),
    field("description", "description", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("unitPrice", "unit_price", "PriceInput", "input"),
    field("unitDiscount", "unit_discount", "PriceInput", "input"),
    field("unitTax", "unit_tax", "ConsumerInvoiceLevyInput", "input"),
    field("unitDuty", "unit_duty", "ConsumerInvoiceLevyInput", "input")
  ]),
  new ModelData("ConsumerInvoiceLineDiscountInput", [
    field("price", "price", "PriceInput", "input")
  ]),
  new ModelData("ConsumerInvoiceReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoicePaymentInput", [
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("description", "description", "String", "scalar"),
    field("value", "value", "PriceInput", "input"),
    field("billingAddress", "billing_address", "BillingAddressInput", "input")
  ]),
  new ModelData("ConsumerInvoiceDocumentInput", [
    field("type", "type", "ConsumerInvoiceDocumentType", "enum"),
    field("language", "language", "String", "scalar"),
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoiceInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("order", "order", "ConsumerInvoiceOrderSummaryInput", "input"),
    field("entity", "entity", "MerchantOfRecordEntityInput", "input"),
    field("payments", "payments", "ConsumerInvoicePaymentInput", "array"),
    field("center", "center", "ConsumerInvoiceCenterReferenceInput", "input"),
    field("destination", "destination", "OrderAddressInput", "input"),
    field("billingAddress", "billing_address", "BillingAddressInput", "input"),
    field("lines", "lines", "ConsumerInvoiceLineInput", "array"),
    field("documents", "documents", "ConsumerInvoiceDocumentInput", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("taxRegistration", "tax_registration", "TaxRegistrationInput", "input"),
    field("customerType", "customer_type", "ConsumerInvoiceCustomerType", "enum"),
    field("estimatedDeliveryDate", "estimated_delivery_date", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("B2bInvoiceReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("CreditMemoInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("invoice", "invoice", "ConsumerInvoiceReferenceInput", "input"),
    field("entity", "entity", "MerchantOfRecordEntityInput", "input"),
    field("payments", "payments", "ConsumerInvoicePaymentInput", "array"),
    field("lines", "lines", "ConsumerInvoiceLineInput", "array"),
    field("documents", "documents", "ConsumerInvoiceDocumentInput", "array"),
    field("attributes", "attributes", "Object", "scalar"),
    field("taxRegistration", "tax_registration", "TaxRegistrationInput", "input")
  ]),
  new ModelData("B2bCreditMemoInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("buyer", "buyer", "MerchantOfRecordEntityInput", "input"),
    field("seller", "seller", "MerchantOfRecordEntityInput", "input"),
    field("status", "status", "ConsumerInvoiceStatus", "enum"),
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("key", "key", "String", "scalar"),
    field("invoice", "invoice", "B2bInvoiceReferenceInput", "input"),
    field("lines", "lines", "ConsumerInvoiceLineInput", "array"),
    field("documents", "documents", "ConsumerInvoiceDocumentInput", "array"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("ConsumerInvoiceLineInput", [
    field("discriminator", "discriminator", "ConsumerInvoiceLineDiscriminator", "enum"),
    field("item", "item", "ItemReferenceInput", "input"),
    field("description", "description", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("unitPrice", "unit_price", "PriceInput", "input"),
    field("unitDiscount", "unit_discount", "PriceInput", "input"),
    field("unitTax", "unit_tax", "ConsumerInvoiceLevyInput", "input"),
    field("unitDuty", "unit_duty", "ConsumerInvoiceLevyInput", "input"),
    field("price", "price", "PriceInput", "input"),
    field("discount", "discount", "PriceInput", "input"),
    field("tax", "tax", "ConsumerInvoiceLevyInput", "input"),
    field("duty", "duty", "ConsumerInvoiceLevyInput", "input")
  ]),
  new ModelData("OrderAddressInput", [
    field("text", "text", "String", "scalar"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("latitude", "latitude", "String", "scalar"),
    field("longitude", "longitude", "String", "scalar"),
    field("contact", "contact", "ContactInput", "input")
  ]),
  new ModelData("OrderSummaryImageInput", [
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("OrderSummaryLineItemInput", [
    field("item", "item", "OrderSummaryItemInput", "input"),
    field("quantity", "quantity", "Long", "scalar"),
    field("discount", "discount", "PriceInput", "input"),
    field("tax", "tax", "OrderSummaryLevyInput", "input"),
    field("duty", "duty", "OrderSummaryLevyInput", "input"),
    field("total", "total", "PriceInput", "input"),
    field("priceAttributes", "price_attributes", "Object", "scalar")
  ]),
  new ModelData("OrderSummaryPriceDetailInput", [
    field("price", "price", "PriceInput", "input"),
    field("name", "name", "String", "scalar"),
    field("rate", "rate", "Decimal", "scalar"),
    field("rateLabel", "rate_label", "String", "scalar")
  ]),
  new ModelData("OrderSummaryItemInput", [
    field("number", "number", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("description", "description", "String", "scalar"),
    field("attributes", "attributes", "CheckoutItemContentAttributeInput", "array"),
    field("image", "image", "OrderSummaryImageInput", "input"),
    field("price", "price", "PriceInput", "input"),
    field("discount", "discount", "PriceInput", "input"),
    field("tax", "tax", "OrderSummaryLevyInput", "input"),
    field("duty", "duty", "OrderSummaryLevyInput", "input"),
    field("priceAttributes", "price_attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutItemContentAttributeInput", [
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("value", "value", "String", "scalar")
  ]),
  new ModelData("OrderSummaryLevyInput", [
    field("rate", "rate", "Decimal", "scalar"),
    field("rateLabel", "rate_label", "String", "scalar"),
    field("value", "value", "PriceInput", "input")
  ]),
  new ModelData("OrderSummaryInput", [
    field("number", "number", "String", "scalar"),
    field("subtotal", "subtotal", "OrderSummaryPriceDetailInput", "input"),
    field("shipping", "shipping", "OrderSummaryPriceDetailInput", "input"),
    field("tax", "tax", "OrderSummaryPriceDetailInput", "input"),
    field("duty", "duty", "OrderSummaryPriceDetailInput", "input"),
    field("insurance", "insurance", "OrderSummaryPriceDetailInput", "input"),
    field("discount", "discount", "OrderSummaryPriceDetailInput", "input"),
    field("surcharges", "surcharges", "OrderSummaryPriceDetailInput", "input"),
    field("adjustment", "adjustment", "OrderSummaryPriceDetailInput", "input"),
    field("total", "total", "OrderSummaryPriceDetailInput", "input"),
    field("lines", "lines", "OrderSummaryLineItemInput", "array"),
    field("identifiers", "identifiers", "Object", "scalar")
  ]),
  new ModelData("TaxRegistrationInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("timestamp", "timestamp", "DateTimeIso8601", "scalar"),
    field("result", "result", "TaxVerificationResult", "enum"),
    field("resultReason", "result_reason", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "String", "scalar"),
    field("companyName", "company_name", "String", "scalar")
  ]),
  new ModelData("OrganizationPutFormInput", [
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input")
  ]),
  new ModelData("OrganizationFormInput", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input")
  ]),
  new ModelData("PublicKeyInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutErrorInput", [
    field("code", "code", "CheckoutErrorCode", "enum"),
    field("messages", "messages", "String", "array"),
    field("redirect", "redirect", "CheckoutRedirectInput", "input")
  ]),
  new ModelData("CheckoutReferenceInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutLineInput", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReferenceInput", "input"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutLineFormInput", [
    field("checkoutId", "checkout_id", "String", "scalar"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutInput", [
    field("id", "id", "String", "scalar"),
    field("organization", "organization", "OrganizationSummaryInput", "input"),
    field("order", "order", "OrderSummaryInput", "input"),
    field("errors", "errors", "CheckoutErrorInput", "array")
  ]),
  new ModelData("CheckoutRedirectInput", [
    field("method", "method", "CheckoutRedirectMethod", "enum"),
    field("url", "url", "String", "scalar"),
    field("body", "body", "String", "scalar")
  ])
]);
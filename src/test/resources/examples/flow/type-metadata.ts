
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
  new EnumData("ConsumerInvoiceDocumentType", [
    new EnumValue("PDF", "pdf")
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
  new ModelData("ItemReference", [
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntityRegistration", [
    field("number", "number", "String", "scalar"),
    field("country", "country", "String", "scalar")
  ]),
  new ModelData("Name", [
    field("first", "first", "String", "scalar"),
    field("last", "last", "String", "scalar")
  ]),
  new ModelData("OrganizationReference", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("Organization", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parent", "parent", "OrganizationReference", "type"),
    field("defaults", "defaults", "OrganizationDefaults", "type"),
    field("createdAt", "created_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("Contact", [
    field("name", "name", "Name", "type"),
    field("company", "company", "String", "scalar"),
    field("email", "email", "String", "scalar"),
    field("phone", "phone", "String", "scalar")
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
  new ModelData("Money", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("Price", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar"),
    field("label", "label", "String", "scalar")
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
  new ModelData("OrganizationDefaults", [
    field("country", "country", "String", "scalar"),
    field("baseCurrency", "base_currency", "String", "scalar"),
    field("language", "language", "String", "scalar"),
    field("locale", "locale", "String", "scalar"),
    field("timezone", "timezone", "String", "scalar")
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
  new ModelData("ConsumerInvoiceLineItem", [
    field("item", "item", "ItemReference", "type"),
    field("description", "description", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("unitPrice", "unit_price", "Price", "type"),
    field("unitDiscount", "unit_discount", "Price", "type"),
    field("unitTax", "unit_tax", "ConsumerInvoiceLevy", "type"),
    field("unitDuty", "unit_duty", "ConsumerInvoiceLevy", "type")
  ]),
  new ModelData("ConsumerInvoiceDocument", [
    field("type", "type", "ConsumerInvoiceDocumentType", "enum"),
    field("language", "language", "String", "scalar"),
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoiceOrderSummary", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("submittedAt", "submitted_at", "DateTimeIso8601", "scalar")
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
  new ModelData("ConsumerInvoiceCenterReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "Address", "type")
  ]),
  new ModelData("ConsumerInvoiceReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoiceLineDiscount", [
    field("price", "price", "Price", "type")
  ]),
  new ModelData("B2bInvoiceReference", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
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
  new ModelData("ConsumerInvoiceLevy", [
    field("rate", "rate", "Decimal", "scalar"),
    field("value", "value", "Price", "type")
  ]),
  new ModelData("ConsumerInvoicePayment", [
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("description", "description", "String", "scalar"),
    field("value", "value", "Price", "type"),
    field("billingAddress", "billing_address", "BillingAddress", "type")
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
  new ModelData("ConsumerInvoiceLineShipping", [
    field("price", "price", "Price", "type"),
    field("discount", "discount", "Price", "type"),
    field("tax", "tax", "ConsumerInvoiceLevy", "type"),
    field("duty", "duty", "ConsumerInvoiceLevy", "type")
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
  new ModelData("OrganizationForm", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaults", "type")
  ]),
  new ModelData("OrganizationPutForm", [
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaults", "type")
  ]),
  new ModelData("PublicKey", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationAttributesForm", [
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutModificationCurrencyForm", [
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("CheckoutModification", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReference", "type"),
    field("form", "form", "CheckoutModificationForm", "union")
  ]),
  new ModelData("CheckoutLineModificationQuantityForm", [
    field("id", "id", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutLineModificationDeleteForm", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationLineForm", [
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressCityForm", [
    field("city", "city", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationLanguageForm", [
    field("language", "language", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationPromotionCodeForm", [
    field("code", "code", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressStreetsForm", [
    field("streets", "streets", "String", "array")
  ]),
  new ModelData("CheckoutLineForm", [
    field("checkoutId", "checkout_id", "String", "scalar"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutLine", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReference", "type"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutLineQuantityForm", [
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressProvinceForm", [
    field("province", "province", "String", "scalar")
  ]),
  new ModelData("Checkout", [
    field("id", "id", "String", "scalar"),
    field("organization", "organization", "OrganizationReference", "type")
  ]),
  new ModelData("CheckoutReference", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressPostalForm", [
    field("postal", "postal", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressCountryForm", [
    field("country", "country", "String", "scalar")
  ]),
  new EnumData("ConsumerInvoiceLineDiscriminator", [
    new EnumValue("ITEM", "item"),
    new EnumValue("DISCOUNT", "discount"),
    new EnumValue("SHIPPING", "shipping")
  ]),
  new EnumData("CheckoutModificationFormDiscriminator", [
    new EnumValue("CHECKOUT_MODIFICATION_ATTRIBUTES_FORM", "checkout_modification_attributes_form"),
    new EnumValue("CHECKOUT_MODIFICATION_CURRENCY_FORM", "checkout_modification_currency_form"),
    new EnumValue("CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_STREETS_FORM", "checkout_modification_destination_address_streets_form"),
    new EnumValue("CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_CITY_FORM", "checkout_modification_destination_address_city_form"),
    new EnumValue("CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_PROVINCE_FORM", "checkout_modification_destination_address_province_form"),
    new EnumValue("CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_POSTAL_FORM", "checkout_modification_destination_address_postal_form"),
    new EnumValue("CHECKOUT_MODIFICATION_DESTINATION_ADDRESS_COUNTRY_FORM", "checkout_modification_destination_address_country_form"),
    new EnumValue("CHECKOUT_MODIFICATION_LANGUAGE_FORM", "checkout_modification_language_form"),
    new EnumValue("CHECKOUT_MODIFICATION_LINE_FORM", "checkout_modification_line_form"),
    new EnumValue("CHECKOUT_LINE_MODIFICATION_DELETE_FORM", "checkout_line_modification_delete_form"),
    new EnumValue("CHECKOUT_LINE_MODIFICATION_QUANTITY_FORM", "checkout_line_modification_quantity_form"),
    new EnumValue("CHECKOUT_MODIFICATION_PROMOTION_CODE_FORM", "checkout_modification_promotion_code_form")
  ]),
  new ModelData("ItemReferenceInput", [
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("MerchantOfRecordEntityRegistrationInput", [
    field("number", "number", "String", "scalar"),
    field("country", "country", "String", "scalar")
  ]),
  new ModelData("NameInput", [
    field("first", "first", "String", "scalar"),
    field("last", "last", "String", "scalar")
  ]),
  new ModelData("OrganizationReferenceInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("OrganizationInput", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parent", "parent", "OrganizationReferenceInput", "input"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input"),
    field("createdAt", "created_at", "DateTimeIso8601", "scalar")
  ]),
  new ModelData("ContactInput", [
    field("name", "name", "NameInput", "input"),
    field("company", "company", "String", "scalar"),
    field("email", "email", "String", "scalar"),
    field("phone", "phone", "String", "scalar")
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
  new ModelData("MoneyInput", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("PriceInput", [
    field("amount", "amount", "Float", "scalar"),
    field("currency", "currency", "String", "scalar"),
    field("label", "label", "String", "scalar")
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
  new ModelData("OrganizationDefaultsInput", [
    field("country", "country", "String", "scalar"),
    field("baseCurrency", "base_currency", "String", "scalar"),
    field("language", "language", "String", "scalar"),
    field("locale", "locale", "String", "scalar"),
    field("timezone", "timezone", "String", "scalar")
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
  new ModelData("ConsumerInvoiceLineItemInput", [
    field("item", "item", "ItemReferenceInput", "input"),
    field("description", "description", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("unitPrice", "unit_price", "PriceInput", "input"),
    field("unitDiscount", "unit_discount", "PriceInput", "input"),
    field("unitTax", "unit_tax", "ConsumerInvoiceLevyInput", "input"),
    field("unitDuty", "unit_duty", "ConsumerInvoiceLevyInput", "input")
  ]),
  new ModelData("ConsumerInvoiceDocumentInput", [
    field("type", "type", "ConsumerInvoiceDocumentType", "enum"),
    field("language", "language", "String", "scalar"),
    field("url", "url", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoiceOrderSummaryInput", [
    field("id", "id", "String", "scalar"),
    field("number", "number", "String", "scalar"),
    field("submittedAt", "submitted_at", "DateTimeIso8601", "scalar")
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
  new ModelData("ConsumerInvoiceCenterReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("address", "address", "AddressInput", "input")
  ]),
  new ModelData("ConsumerInvoiceReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
  ]),
  new ModelData("ConsumerInvoiceLineDiscountInput", [
    field("price", "price", "PriceInput", "input")
  ]),
  new ModelData("B2bInvoiceReferenceInput", [
    field("id", "id", "String", "scalar"),
    field("key", "key", "String", "scalar"),
    field("number", "number", "String", "scalar")
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
  new ModelData("ConsumerInvoiceLevyInput", [
    field("rate", "rate", "Decimal", "scalar"),
    field("value", "value", "PriceInput", "input")
  ]),
  new ModelData("ConsumerInvoicePaymentInput", [
    field("date", "date", "DateTimeIso8601", "scalar"),
    field("description", "description", "String", "scalar"),
    field("value", "value", "PriceInput", "input"),
    field("billingAddress", "billing_address", "BillingAddressInput", "input")
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
  new ModelData("ConsumerInvoiceLineShippingInput", [
    field("price", "price", "PriceInput", "input"),
    field("discount", "discount", "PriceInput", "input"),
    field("tax", "tax", "ConsumerInvoiceLevyInput", "input"),
    field("duty", "duty", "ConsumerInvoiceLevyInput", "input")
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
  new ModelData("OrganizationFormInput", [
    field("id", "id", "String", "scalar"),
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input")
  ]),
  new ModelData("OrganizationPutFormInput", [
    field("name", "name", "String", "scalar"),
    field("environment", "environment", "Environment", "enum"),
    field("parentId", "parent_id", "String", "scalar"),
    field("defaults", "defaults", "OrganizationDefaultsInput", "input")
  ]),
  new ModelData("PublicKeyInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationAttributesFormInput", [
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutModificationCurrencyFormInput", [
    field("currency", "currency", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationInput", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReferenceInput", "input"),
    field("form", "form", "CheckoutModificationFormInput", "input")
  ]),
  new ModelData("CheckoutLineModificationQuantityFormInput", [
    field("id", "id", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutLineModificationDeleteFormInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationLineFormInput", [
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressCityFormInput", [
    field("city", "city", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationLanguageFormInput", [
    field("language", "language", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationPromotionCodeFormInput", [
    field("code", "code", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressStreetsFormInput", [
    field("streets", "streets", "String", "array")
  ]),
  new ModelData("CheckoutLineFormInput", [
    field("checkoutId", "checkout_id", "String", "scalar"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutLineInput", [
    field("id", "id", "String", "scalar"),
    field("checkout", "checkout", "CheckoutReferenceInput", "input"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("attributes", "attributes", "Object", "scalar")
  ]),
  new ModelData("CheckoutLineQuantityFormInput", [
    field("quantity", "quantity", "Long", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressProvinceFormInput", [
    field("province", "province", "String", "scalar")
  ]),
  new ModelData("CheckoutInput", [
    field("id", "id", "String", "scalar"),
    field("organization", "organization", "OrganizationReferenceInput", "input")
  ]),
  new ModelData("CheckoutReferenceInput", [
    field("id", "id", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressPostalFormInput", [
    field("postal", "postal", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationDestinationAddressCountryFormInput", [
    field("country", "country", "String", "scalar")
  ]),
  new ModelData("CheckoutModificationFormInput", [
    field("discriminator", "discriminator", "CheckoutModificationFormDiscriminator", "enum"),
    field("attributes", "attributes", "Object", "scalar"),
    field("currency", "currency", "String", "scalar"),
    field("streets", "streets", "String", "array"),
    field("city", "city", "String", "scalar"),
    field("province", "province", "String", "scalar"),
    field("postal", "postal", "String", "scalar"),
    field("country", "country", "String", "scalar"),
    field("language", "language", "String", "scalar"),
    field("itemNumber", "item_number", "String", "scalar"),
    field("quantity", "quantity", "Long", "scalar"),
    field("id", "id", "String", "scalar"),
    field("code", "code", "String", "scalar")
  ])
]);
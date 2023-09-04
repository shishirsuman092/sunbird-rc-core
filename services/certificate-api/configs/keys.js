const publicKeyPem = process.env.CERTIFICATE_PUBLIC_KEY || '';
// eslint-disable-next-line max-len
const privateKeyPem = process.env.CERTIFICATE_PRIVATE_KEY || '';
const qrType = process.env.QR_TYPE || 'W3C-VC';
const certDomainUrl = process.env.CERTIFICATE_DOMAIN_URL || "https://registration.uphrh.in/claim-ms";
const smsAuthKey = "";
module.exports = {
  publicKeyPem,
  privateKeyPem,
  smsAuthKey,
  qrType,
  certDomainUrl
};

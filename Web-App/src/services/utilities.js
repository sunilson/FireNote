const sha1 = require('js-sha1');

export function hashPassword(password) {
  return sha1.hex(password);
}

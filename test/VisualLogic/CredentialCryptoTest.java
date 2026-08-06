/*
 * MyOpenLab by Carmelo Salafia www.myopenlab.de
 * Copyright (C) 2004  Carmelo Salafia cswi@gmx.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package VisualLogic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CredentialCryptoTest {

    @Test
    public void roundTripPreservesPlaintext() {
        String[] samples = {"password123", "p@ss wörd", "x",
            "a long credential string with special chars !#$%^&*()"};
        for (String sample : samples) {
            assertEquals(sample, CredentialCrypto.decrypt(CredentialCrypto.encrypt(sample)));
        }
    }

    @Test
    public void encryptEmptyOrNullReturnsEmpty() {
        assertEquals("", CredentialCrypto.encrypt(""));
        assertEquals("", CredentialCrypto.encrypt(null));
    }

    @Test
    public void decryptEmptyOrNullReturnsUnchanged() {
        assertEquals("", CredentialCrypto.decrypt(""));
        assertNull(CredentialCrypto.decrypt(null));
    }

    @Test
    public void decryptNonEncryptedPassthrough() {
        assertEquals("plain", CredentialCrypto.decrypt("plain"));
    }

    @Test
    public void decryptTamperedPayloadPassthrough() {
        String encrypted = CredentialCrypto.encrypt("secret");
        String base64 = encrypted.substring("enc:v1:".length());
        String corrupted = base64.substring(0, base64.length() - 4) + "AAAA";
        String stored = "enc:v1:" + corrupted;
        assertEquals(stored, CredentialCrypto.decrypt(stored));
    }

    @Test
    public void encryptProducesRandomCiphertext() {
        String a = CredentialCrypto.encrypt("same-input");
        String b = CredentialCrypto.encrypt("same-input");
        assertTrue(a.startsWith("enc:v1:"));
        assertFalse(a.equals(b));
    }
}

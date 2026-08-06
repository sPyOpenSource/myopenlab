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
package MyParser;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class ExpressionTest {

    @Before
    public void clearTokenList() {
        Expression.liste.clear();
    }

    private List<?> parse(String input) throws Exception {
        Expression.liste.clear();
        new Expression().yyparse(new Expression.Scanner(new StringReader(input)), null);
        return Expression.liste;
    }

    @Test
    public void parsesAddition() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "PUSHB 2", "ADD"), parse("1+2"));
    }

    @Test
    public void parsesAssignment() throws Exception {
        assertEquals(Arrays.asList("PUSHB 5", "POPI a"), parse("a=5"));
    }

    @Test
    public void parsesTrigFunction() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "SIN "), parse("SIN(1)"));
    }

    @Test
    public void parsesRelationalExpression() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "PUSHB 2", "IF_A<B "), parse("1<2"));
    }

    @Test
    public void rejectsMalformedInput() throws Exception {
        Expression.liste.clear();
        try {
            new Expression().yyparse(new Expression.Scanner(new StringReader("1+")), null);
            fail("expected Expression.yyException");
        } catch (Expression.yyException expected) {
            // expected
        }
    }
}
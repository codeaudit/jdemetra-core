/*
* Copyright 2013 National Bank of Belgium
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
* by the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in writing, software 
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and 
* limitations under the Licence.
*/

package ec.satoolkit;

import ec.tstoolkit.design.Development;

/**
 * Type of decomposition
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Beta)
public enum DecompositionMode {

    /**
     * Decomposition that doesn't correspond to the next definitions
     */
    Undefined,
    /**
     * Y = T + S + I, SA = Y - S = T + I
     */
    Additive,
    /**
     * Y = T * S * I, SA = Y / S = T * I
     */
    Multiplicative,
    /**
     * Log(Y) = T + S + I, SA = exp( T + I) = Y / exp(S)
     */
    LogAdditive;

    public boolean isMultiplicative() {
        return this == Multiplicative || this == LogAdditive;
    }
}

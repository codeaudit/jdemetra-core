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


package ec.tstoolkit.data;

import ec.tstoolkit.design.Development;

/**
 * Represents a read/write block of doubles
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
public interface IDataBlock extends IReadDataBlock {
    /**
     * Copies data from a given buffer. The buffer must contain enough data
     * for filling this object (it can be larger).
     * @param buffer The buffer containing the data
     * @param start The position in the buffer of the first data being copied
     */
    public void copyFrom(double[] buffer, int start);

    /**
     * Extracts a new data block from an existing data block
     * @param start The position of the first extracted data
     * @param length The number of data being extracted
     * @return A new data block is returned, except when the extract is equivalent to 
     * the current object. In that case, the existing data block can be returned.
     */
    public IDataBlock extract(int start, int length);

    /**
     * Sets an element of the data block to a given value
     * @param idx The position of the element being modified.
     * @param value The new value.
     */
    public void set(int idx, double value);

}

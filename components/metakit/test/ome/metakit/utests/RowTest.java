//
// RowTest.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ome.metakit.utests;

import java.io.IOException;

import ome.metakit.MetakitReader;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/componets/metakit/test/ome/metakit/utests/RowTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/test/ome/metakit/utests/RowTest.java">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class RowTest {

  private static final String INVALID_TABLE = "this cannot be a valid table";

  private MetakitReader reader;

  @BeforeMethod
  public void setUp() throws IOException {
    reader = new MetakitReader(System.getProperty("filename"));
  }

  @Test
  public void testDataTypes() {
    int tableCount = reader.getTableCount();
    for (int table=0; table<tableCount; table++) {
      Object[][] data = reader.getTableData(table);
      Class[] columnTypes = reader.getColumnTypes(table);

      for (int row=0; row<data.length; row++) {
        for (int col=0; col<data[row].length; col++) {
          assertTrue(columnTypes[col].isInstance(data[row][col]));
        }
      }
    }
  }

  @Test
  public void testTableDataConsistentByIndex() {
    int tableCount = reader.getTableCount();
    for (int table=0; table<tableCount; table++) {
      Object[][] bulkData = reader.getTableData(table);

      for (int row=0; row<bulkData.length; row++) {
        Object[] rowData = reader.getRowData(row, table);

        assertNotNull(rowData);
        assertEquals(bulkData[row].length, rowData.length);

        for (int col=0; col<bulkData[row].length; col++) {
          assertEquals(bulkData[row][col], rowData[col]);
        }
      }
    }
  }

  @Test
  public void testTableDataConsistentByName() {
    String[] tableNames = reader.getTableNames();
    for (int table=0; table<tableNames.length; table++) {
      String name = tableNames[table];
      Object[][] bulkData = reader.getTableData(name);

      for (int row=0; row<bulkData.length; row++) {
        Object[] rowData = reader.getRowData(row, name);

        assertNotNull(rowData);
        assertEquals(bulkData[row].length, rowData.length);

        for (int col=0; col<bulkData[row].length; col++) {
          assertEquals(bulkData[row][col], rowData[col]);
        }
      }
    }
  }

  @Test
  public void testTableDataConsistentByRow() {
    String[] tableNames = reader.getTableNames();
    for (int table=0; table<tableNames.length; table++) {
      int rowCount = reader.getRowCount(table);
      assertEquals(rowCount, reader.getRowCount(tableNames[table]));

      for (int row=0; row<rowCount; row++) {
        Object[] rowByIndex = reader.getRowData(row, table);
        Object[] rowByName = reader.getRowData(row, tableNames[table]);

        assertNotNull(rowByIndex);
        assertNotNull(rowByName);
        assertEquals(rowByIndex.length, rowByName.length);

        for (int col=0; col<rowByIndex.length; col++) {
          assertEquals(rowByIndex[col], rowByName[col]);
        }
      }
    }
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataTableIndexTooSmall() {
    reader.getRowData(0, -1);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataTableIndexTooLarge() {
    reader.getRowData(0, reader.getTableCount());
  }

  @Test
  public void testRowDataInvalidTableNames() {
    assertNull(reader.getRowData(0, null));
    assertNull(reader.getRowData(0, INVALID_TABLE));
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataByIndexIndexTooSmall() {
    reader.getRowData(-1, 0);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataByIndexIndexTooLarge() {
    reader.getRowData(reader.getRowCount(0), 0);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataByNameIndexTooSmall() {
    String tableName = reader.getTableNames()[0];
    reader.getRowData(-1, tableName);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testRowDataByNameIndexTooLarge() {
    String tableName = reader.getTableNames()[0];
    reader.getRowData(reader.getRowCount(tableName), tableName);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testDataTableIndexTooSmall() {
    reader.getTableData(-1);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testDataTableIndexTooLarge() {
    reader.getTableData(reader.getTableCount());
  }

  @Test
  public void testDataInvalidTableNames() {
    assertNull(reader.getTableData(null));
    assertNull(reader.getTableData(INVALID_TABLE));
  }

}

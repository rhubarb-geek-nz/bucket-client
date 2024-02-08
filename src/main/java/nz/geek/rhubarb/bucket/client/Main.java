/*
 *
 *  Copyright 2024, Roger Brown
 *
 *  This file is part of bucket-client.
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your
 *  option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package nz.geek.rhubarb.bucket.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author rogerb
 *         The goal of this class is to only use primitive Java features from
 *         JDK 1.2 in order to be useable to bootstrap legacy machines. It is
 *         intented to be used with a simple HTTP server such as
 *         rhubarb-geek-nz/bucket.
 */
public class Main {
  public static void main(String[] args) throws IOException {
    URL url = null;
    int i = 0;
    List headers = new ArrayList();
    String file = null, method = "GET";

    while (i < args.length) {
      String p = args[i++];

      if ("-url".equals(p)) {
        url = new URL(args[i++]);
        continue;
      }

      if ("-header".equals(p)) {
        String name = args[i++];
        String value = args[i++];
        headers.add(new String[] { name, value });
        continue;
      }

      if ("-file".equals(p)) {
        file = args[i++];
        continue;
      }

      if ("-method".equals(p)) {
        method = args[i++];
        continue;
      }

      throw new IllegalArgumentException(p);
    }

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(method);

    Iterator it = headers.iterator();

    while (it.hasNext()) {
      String[] pair = (String[]) it.next();
      connection.setRequestProperty(pair[0], pair[1]);
    }

    if ("GET".equals(method) || "DELETE".equals(method)) {
      int status = connection.getResponseCode();

      if ((status < 200) || (status > 205)) {
        throw new IOException(connection.getResponseMessage());
      }

      InputStream is = connection.getInputStream();
      try {
        OutputStream os = (file == null) ? (OutputStream) System.out : new FileOutputStream(file);

        try {
          transfer(is, os);
        } finally {
          if (file != null) {
            os.close();
          }
        }
      } finally {
        is.close();
      }
    } else {
      if ("PUT".equals(method) || "POST".equals(method)) {
        if (file != null) {
          long len = new File(file).length();
          connection.setRequestProperty("Content-Length", Long.toString(len));
        }

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        try {
          InputStream is = (file == null) ? System.in : new FileInputStream(file);

          try {
            transfer(is, os);
          } finally {
            if (file != null) {
              is.close();
            }
          }
        } finally {
          os.close();
        }
      } else {
        throw new IllegalArgumentException(method);
      }

      int status = connection.getResponseCode();

      if ((status < 200) || (status > 205)) {
        throw new IOException(connection.getResponseMessage());
      }

      if (status == 200) {
        InputStream is = connection.getInputStream();

        try {
          transfer(is, System.out);
        } finally {
          is.close();
        }
      }
    }
  }

  static void transfer(InputStream is, OutputStream os) throws IOException {
    byte[] buffer = new byte[4096];
    while (true) {
      int i = is.read(buffer);
      if (i < 1)
        break;
      os.write(buffer, 0, i);
    }
  }
}

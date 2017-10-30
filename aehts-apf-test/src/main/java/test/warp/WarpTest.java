/**
 * Copyright 2012-2017 Functional Genomics Development Team, European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @author Andrew Tikhonov <andrew.tikhonov@gmail.com>
 **/
package test.warp;

import com.jhlabs.image.WarpFilter;
import com.jhlabs.image.WarpGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 07/06/2012
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class WarpTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public WarpTest() {

        try {
            //BufferedImage image = ImageIO.read(new File("/Users/andrew/project/aehts-apf/test/x_32378081.jpg"));
            //WarpFilter filter = new WarpFilter();
            //WarpGrid src = new WarpGrid();
            //WarpGrid dst = new WarpGrid();
            //filter.crossDissolve();


        } catch (Exception ex) {
            log.error("Error!", ex);
        }
    }
    public static void main(String[] args){
        new WarpTest();
    }
}

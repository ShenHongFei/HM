import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class Temp{
    static void main(String... args){
        DefaultKaptcha kt = new DefaultKaptcha()
        kt.config=new Config(new Properties())

        def text = kt.createText()
        println(text)
        def image = kt.createImage(text)
        ImageIO.write(image,'jpg',new File("${text}.img"))

    }
}
 
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import java.awt.*;
import java.awt.event.KeyEvent;

public class BotVisual {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        try {
            String tempBtnPesquisa = "src/resources/btn_pesquisa_windows.png";
            String imgAreaTrabalho = "src/resources/area_de_trabalho.png";

            capturarImagem(imgAreaTrabalho);

            Point localizacaoBotao = procuraImagemNaTela(imgAreaTrabalho, tempBtnPesquisa);

            if (localizacaoBotao != null) {
                Robot robot = new Robot();
                robot.mouseMove((int) localizacaoBotao.x, (int) localizacaoBotao.y);
                robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

                Thread.sleep(1000);

                digitarTexto(robot, "VsCode");

                Thread.sleep(1000);

                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } else {
                System.out.println("Botão de pesquisa não encontrado!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void capturarImagem(String caminhoSaida) throws Exception {
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        java.awt.image.BufferedImage imagemCapturada = robot.createScreenCapture(rect);
        javax.imageio.ImageIO.write(imagemCapturada, "png", new java.io.File(caminhoSaida));
    }

    private static Point procuraImagemNaTela(String caminhoImagem, String caminhoTemp) {
        Mat tela = Imgcodecs.imread(caminhoImagem);
        Mat temp = Imgcodecs.imread(caminhoTemp);

        int colunas = tela.cols() - tela.cols() + 1;
        int linhas = tela.rows() - tela.rows() + 1;
        Mat resultado = new Mat(linhas, colunas, CvType.CV_32FC1);

        Imgproc.matchTemplate(tela, temp, resultado, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(resultado);

        if (mmr.maxVal >= 0.8) {
            Point localizacao = mmr.maxLoc;
            return new Point(localizacao.x + temp.cols() / 2, localizacao.y + temp.rows() / 2);
        }
        return null;
    }

    private static void digitarTexto(Robot robot, String texto) {
        for (char c : texto.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character: " + c);
            }
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
    }
}

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import ij.plugin.ImageCalculator;
import ij.plugin.ContrastEnhancer;
import java.awt.AWTEvent;
import java.awt.Scrollbar;
import java.util.List;
import java.util.ArrayList;

public class Naive_Background_Correction implements ExtendedPlugInFilter, DialogListener {

    private static int FLAGS = DOES_ALL | NO_UNDO;
    private static double radius = 2.0;
    private static int iterations = 10;
    private GenericDialog gd;

    public int setup (String arg, ImagePlus imp) {
        return FLAGS;
    }

    public boolean dialogItemChanged (GenericDialog gd, AWTEvent e) {
        radius = gd.getNextNumber();
        Scrollbar scrollbar = (Scrollbar) gd.getSliders().get(0);
        iterations = scrollbar.getValue();
        return !gd.invalidNumber(); //input is valid if all numeric input is ok
    }

    public void setNPasses (int nPasses) {}

    public int showDialog (ImagePlus imp, String command, PlugInFilterRunner pfr) {
        gd = new GenericDialog("Naive Background Correction");
        gd.addNumericField("Radius:", 3.00, 2);
        gd.addSlider("Iteration",1,30,15);
        gd.addCheckbox("Subtract background",true);
        gd.addCheckbox("Divide background",false);
        gd.addPreviewCheckbox(pfr);
        gd.addDialogListener(this);
        gd.showDialog();
        if (gd.wasCanceled()) return DONE;
        IJ.register(this.getClass()); // protect static class variables (parameters) from garbage collection
        return IJ.setupDialog(imp, FLAGS);
    }

    public void run(ImageProcessor ip) {
        ImagePlus imp = IJ.getImage();
        if (null == imp) return;

        // Actually do the work. A list is used because multiple images can be returned (subtraction and division)
        List<ImagePlus> result = exec(imp, radius, iterations);

        if (null != result) {
            for(ImagePlus image : result){
                image.show();
            }
        }
    }

    private List<ImagePlus> exec(ImagePlus imp, double radius, int iterations) {
        // Check sanity of parameters
        if (null == imp) return null;
        if (radius <= 0) return null;
        if (iterations < 1) return null;

        List<ImagePlus> imageList = new ArrayList<>();
        if (gd.wasOKed()) {
            // If OK was pressed, perform the blur
            ImagePlus blurred = blurred(imp.duplicate(), radius, iterations);
            ImageCalculator ic = new ImageCalculator();
            ContrastEnhancer contrastEnhancer = new ContrastEnhancer();
            contrastEnhancer.setNormalize(true);
            // Create the subtraction image if requested
            if (gd.getNextBoolean()) {
                ImagePlus im_sub = ic.run("Subtract create", imp, blurred);
                contrastEnhancer.stretchHistogram(im_sub, 0.3);
                imageList.add(im_sub);
            }
            // Create the division image if requested
            if (gd.getNextBoolean()) {
                ImagePlus im_div = ic.run("Divide create", imp, blurred);
                contrastEnhancer.stretchHistogram(im_div, 0.3);
                imageList.add(im_div);
            }
            return imageList;
        } else {
            // Okay was not pressed, just update the preview
            ImagePlus blurred = blurred(imp, radius, iterations);
            imageList.add(blurred);
            return imageList;
        }
    }

    private static ImagePlus blurred(ImagePlus imp, double radius, int iterations) {
        for(int i=0; i<iterations; i++)
        {
            imp.getProcessor().blurGaussian(radius);
        }
        return imp;
    }

    /**
     * Main method for debugging.
     * <p>
     * For debugging, it is convenient to have a method that starts ImageJ, loads
     * an image and calls the plugin, e.g. after setting breakpoints.
     * This is taken from https://github.com/imagej/example-legacy-plugin
     *
     * @param args unused
     */
    public static void main(String[] args) {
        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = Naive_Background_Correction.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();

        // Uncomment these lines and put in the path to a test image to auto load the image and then run the plugin
        //ImagePlus image = IJ.openImage("path");
        //image.show();

        // run the plugin
        //IJ.runPlugIn(clazz.getName(), "");
    }
}

package hortifrut;

import java.awt.Frame;
import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.*;
import com.sun.j3d.utils.image.TextureLoader;

public class FrutaEscala extends Applet {

    SimpleUniverse simpleU;

    public void init() {
        setLayout(new BorderLayout());
        //se crea un canvas3D con la configuracion basica
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        //se centra la vista
        add("Center", canvas);
        // se crea un iniverso simple segun la receta corta

        simpleU = new SimpleUniverse(canvas); // setup the SimpleUniverse, attach the Canvas3D
        //se crea un grupo de ramas con la rama principal o raiz
        BranchGroup scene = createSceneGraph();

        /* se crea un TransformGroup con la finalidad de poner la vista en el lugar deceado*/
        TransformGroup tg = simpleU.getViewingPlatform().getViewPlatformTransform();
        Transform3D t3d = new Transform3D();
        /* se crea una translacion en el centro y se aleja del eje z para cudarar el 
            tamanio del escenario es decir aleja la vista para que los objetos se vean mas pequenos 
         */
        t3d.setTranslation(new Vector3f(0f, 0f, 5.2f));
        // se agreha la Traslacion a la vista
        tg.setTransform(t3d);

        scene.compile();
        simpleU.addBranchGraph(scene); //se agrega escena a el SimpleUniverse 

    }

    public BranchGroup createSceneGraph() {
        //Crea la raíz de la rama grafica
        BranchGroup objRoot = new BranchGroup();

        /* se crean dos objetos de tipo Transform3D uno para escalar la fruta y la otra
        para posicionarla en un logar espesifico de del espacio o escenario*/
        Transform3D posicion = new Transform3D();
        Transform3D escala = new Transform3D();

        //escala la fruta
        escala.set(0.6);

        //posicion de la fruta
        posicion.setTranslation(new Vector3f(1.3f, 0f, 0f));

        //se multiplican las matrices es decir se combinan las dos transformaciones
        escala.mul(posicion);

        /* se usa un cargador para cargar el archivo .obj que contiene los vertices e informacion de la 
        manzana previamente diseñada en 3D*/
        Scene s = null;
        try {
            ObjectFile f = new ObjectFile();
            f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            s = f.load("manzana2.obj");
        } catch (java.io.FileNotFoundException ex) {
            System.out.println("no se encontro el archivo");
        }

        //ahora se crea un grupo de transformaciones y se le agrega las transformaciones previas
        TransformGroup objRotate = new TransformGroup(escala);

        /*se crea el nodo de grupo de transformacion */
        TransformGroup objSpin = new TransformGroup();

        /* se habilita la capacidad TRANSFORM_WRITE para que
          nuestro código de comportamiento puede modificarlo en tiempo de ejecución.*/
        objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        /*se agrega a la raiz el grupo de transformaciones*/
        objRoot.addChild(objRotate);

        /*al grupo de transformaciones se le agrega un sub grupo de transfomracion
        que es el que contendra la futa en si*/
        objRotate.addChild(objSpin);

        //se inserta la manzana en el TransformGroup objSpin
        objSpin.addChild(s.getSceneGroup());

        Transform3D zAxis = new Transform3D();

        //funcion de tiempos se indica una onda seno con periodo de 5 segundos
        Alpha alpha6Obj = new Alpha(-1, //cycles
                Alpha.INCREASING_ENABLE //mode
                | Alpha.DECREASING_ENABLE,
                0, //trigger time
                0, //phase delay
                2500,//cycle duration up
                1000,//ramp duration for up
                0, //duration at one
                2500,//cycle duration down
                1000,//ramp duration for down
                0);  //duration at zero

        
        /*clase predefinida en java3d para animaciones de esala*/
//        public ScaleInterpolator(Alpha alpha,
//                            TransformGroup target,
//                            Transform3D axisOfTransform,
//                            float startPosition,
//                            float endPosition)
//  
        ScaleInterpolator escalador
                = new ScaleInterpolator(alpha6Obj, objSpin, zAxis, 1.0f, 2.0f);

        // se crea una esfera delimitadora especifica una región donde un comportamiento está activo
        BoundingSphere bounds = new BoundingSphere();
        escalador.setSchedulingBounds(bounds);
        objSpin.addChild(escalador);

        /**
         * imagen*
         */
        //se crea una apariencia
        Appearance polygon1Appearance = new Appearance();

        //se crea un poligono con puntos de 3 dimesiones con haciendo ilucion a las 4 esquinas de la pantalla
        QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
        polygon1.setCoordinate(0, new Point3f(-3f, -2f, -2f));
        polygon1.setCoordinate(1, new Point3f(3f, -2f, -2f));
        polygon1.setCoordinate(2, new Point3f(3f, 2f, -2f));
        polygon1.setCoordinate(3, new Point3f(-3f, 2f, -2f));

        //se envian las posiciones de las texturas
        polygon1.setTextureCoordinate(0, new Point2f(0.0f, 0.0f));
        polygon1.setTextureCoordinate(1, new Point2f(1.0f, 0.0f));
        polygon1.setTextureCoordinate(2, new Point2f(1.0f, 1.0f));
        polygon1.setTextureCoordinate(3, new Point2f(0.0f, 1.0f));

        //se lee la imagen
        Texture texImage = new TextureLoader("manzana.jpg", this).getTexture();

        //se agrega la imagen a la apariencia
        polygon1Appearance.setTexture(texImage);

        //finalmente se agrega el poligono y su apariencia al la rama principal
        objRoot.addChild(new Shape3D(polygon1, polygon1Appearance));

        return objRoot;
    }

    public void destroy() {
        simpleU.removeAllLocales();
    }

    public static void main(String[] args) {
        // se crea un frame de 511 x 341
        Frame frame = new MainFrame(new FrutaEscala(), 511, 341);
    }
}

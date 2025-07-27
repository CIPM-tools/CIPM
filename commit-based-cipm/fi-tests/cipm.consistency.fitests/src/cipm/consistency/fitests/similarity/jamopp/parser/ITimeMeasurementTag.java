package cipm.consistency.fitests.similarity.jamopp.parser;

/**
 * A placeholder interface meant to be extended by enums that contain tags for
 * taking time measurements. <br>
 * <br>
 * The entire purpose of this interface is to allow implementing further enums
 * for such tags and to allow them to be integrated. Therefore, this interface
 * provides no further functionality, it merely unifies all such tags under a
 * super-type. <br>
 * <br>
 * It is important to provide this interface, as time measurements may include
 * different operations that are important to analyse and may therefore require
 * further tags. With the help of this interface, it becomes possible to
 * implement user-defined tags for future time measurements and to integrate
 * them, without having to modify any pre-existing tags' enums.
 * 
 * @author Alp Torac Genc
 */
public interface ITimeMeasurementTag {
}

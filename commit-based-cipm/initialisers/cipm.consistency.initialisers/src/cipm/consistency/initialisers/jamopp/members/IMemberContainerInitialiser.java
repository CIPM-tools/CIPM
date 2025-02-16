package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.MemberContainer;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

/**
 * An interface meant to be implemented by initialisers, which are supposed to
 * create {@link MemberContainer} instances. <br>
 * <br>
 * {@code mc.createField(...)} method internally creates a {@link Field}, adds
 * it to the calling member container mc and returns the said field. It
 * introduces no new ways to modify the calling member container. <br>
 * <br>
 * Getter methods of {@link MemberContainer} do not return modifiable lists,
 * except for {@code mc.getMembers()} and {@code mc.getDefaultMembers()}.
 * 
 * @author Alp Torac Genc
 */
public interface IMemberContainerInitialiser extends ICommentableInitialiser {
	@Override
	public MemberContainer instantiate();

	public default boolean addMember(MemberContainer mc, Member mem) {
		if (mem != null) {
			mc.getMembers().add(mem);
			return mc.getMembers().contains(mem);
		}
		return true;
	}

	public default boolean addMembers(MemberContainer mc, Member[] mems) {
		return this.doMultipleModifications(mc, mems, this::addMember);
	}

	public default boolean addDefaultMember(MemberContainer mc, Member defMem) {
		if (defMem != null) {
			mc.getDefaultMembers().add(defMem);
			return mc.getDefaultMembers().contains(defMem);
		}
		return true;
	}

	public default boolean addDefaultMembers(MemberContainer mc, Member[] defMems) {
		return this.doMultipleModifications(mc, defMems, this::addDefaultMember);
	}
}

package j2w.team.modules.hotfix.legend.dalvik;

import java.lang.reflect.Method;

import j2w.team.modules.hotfix.legend.utility.LegendNative;
import j2w.team.modules.hotfix.legend.utility.Struct;
import j2w.team.modules.hotfix.legend.utility.StructMapping;
import j2w.team.modules.hotfix.legend.utility.StructMember;

public class DalvikMethodStruct extends Struct {

	@StructMapping(offset = 0) public StructMember				clazz;

	@StructMapping(offset = 4) public StructMember				accessFlags;

	@StructMapping(offset = 8, length = 2) public StructMember	methodIndex;

	@StructMapping(offset = 10, length = 2) public StructMember	registersSize;

	@StructMapping(offset = 12, length = 2) public StructMember	outsSize;

	@StructMapping(offset = 14, length = 2) public StructMember	insSize;

	@StructMapping(offset = 16) public StructMember				name;

	// struct DexProto {
	@StructMapping(offset = 20) public StructMember				dexFile;

	@StructMapping(offset = 24) public StructMember				protoIdx;

	// }

	@StructMapping(offset = 28) public StructMember				shorty;

	@StructMapping(offset = 32) public StructMember				insns;

	@StructMapping(offset = 36) public StructMember				jniArgInfo;

	@StructMapping(offset = 40) public StructMember				nativeFunc;

	/**
	 * The target method
	 */
	private Method												method;

	private DalvikMethodStruct(Method method) {
		super(LegendNative.getMethodAddress(method));
		this.method = method;
	}

	/**
	 * Create a dalvik struct in java mapping native
	 * 
	 * @param method
	 *            method
	 * @return
	 */
	public static DalvikMethodStruct of(Method method) {
		return new DalvikMethodStruct(method);
	}

}

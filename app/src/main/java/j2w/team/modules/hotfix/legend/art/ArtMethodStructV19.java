package j2w.team.modules.hotfix.legend.art;

import java.lang.reflect.Method;

import j2w.team.modules.hotfix.legend.utility.StructMapping;
import j2w.team.modules.hotfix.legend.utility.StructMember;

public class ArtMethodStructV19 extends ArtMethod {

	@StructMapping(offset = 0) private StructMember		klass_;

	@StructMapping(offset = 4) private StructMember		monitor_;

	@StructMapping(offset = 8) private StructMember		declaring_class_;

	@StructMapping(offset = 12) private StructMember	dex_cache_initialized_static_storage_;

	@StructMapping(offset = 16) private StructMember	dex_cache_resolved_methods_;

	@StructMapping(offset = 20) private StructMember	dex_cache_resolved_types_;

	@StructMapping(offset = 24) private StructMember	dex_cache_strings_;

	@StructMapping(offset = 28) private StructMember	access_flags_;

	@StructMapping(offset = 32) private StructMember	code_item_offset_;

	@StructMapping(offset = 36) private StructMember	core_spill_mask_;

	@StructMapping(offset = 40) private StructMember	entry_point_from_compiled_code_;

	@StructMapping(offset = 44) private StructMember	entry_point_from_interpreter_;

	@StructMapping(offset = 48) private StructMember	fp_spill_mask_;

	@StructMapping(offset = 52) private StructMember	frame_size_in_bytes_;

	@StructMapping(offset = 56) private StructMember	gc_map_;

	@StructMapping(offset = 60) private StructMember	mapping_table_;

	@StructMapping(offset = 64) private StructMember	method_dex_index_;

	@StructMapping(offset = 68) private StructMember	method_index_;

	@StructMapping(offset = 72) private StructMember	native_method_;

	@StructMapping(offset = 76) private StructMember	vmap_table_;

	public ArtMethodStructV19(Method method) {
		super(method);
	}

	@Override public long getEntryPointFromInterpreter() {
		return entry_point_from_interpreter_.readLong();
	}

	@Override public void setEntryPointFromInterpreter(long pointer_entry_point_from_interpreter) {
		entry_point_from_interpreter_.write(pointer_entry_point_from_interpreter);
	}

	@Override public long getEntryPointFromJni() {
		return native_method_.readLong();
	}

	@Override public void setEntryPointFromJni(long pointer_entry_point_from_jni) {
		native_method_.write(pointer_entry_point_from_jni);
	}

	@Override public long getEntryPointFromQuickCompiledCode() {
		return entry_point_from_compiled_code_.readLong();
	}

	@Override public void setEntryPointFromQuickCompiledCode(long pointer_entry_point_from_quick_compiled_code) {
		entry_point_from_compiled_code_.write(pointer_entry_point_from_quick_compiled_code);
	}

	@Override public int getAccessFlags() {
		return access_flags_.readInt();
	}

	@Override public void setAccessFlags(int newFlags) {
		access_flags_.write(newFlags);
	}

	@Override public long getDeclaringClass() {
		return declaring_class_.readLong();
	}

	@Override public void setDeclaringClass(long declaringClass) {
		declaring_class_.write(declaringClass);
	}

	@Override public long getDexCacheResolvedMethods() {
		return dex_cache_resolved_methods_.readLong();
	}

	@Override public void setDexCacheResolvedMethods(long pointer_dex_cache_resolved_methods_) {
		dex_cache_resolved_methods_.write(pointer_dex_cache_resolved_methods_);
	}

	@Override public long getDexCacheResolvedTypes() {
		return dex_cache_resolved_types_.readLong();
	}

	@Override public void setDexCacheResolvedTypes(long pointer_dex_cache_resolved_types_) {
		dex_cache_resolved_types_.write(pointer_dex_cache_resolved_types_);
	}

	@Override public int getDexCodeItemOffset() {
		return code_item_offset_.readInt();
	}

	@Override public void setDexCodeItemOffset(int offset) {
		code_item_offset_.write(offset);
	}

	@Override public int getDexMethodIndex() {
		return method_dex_index_.readInt();
	}

	@Override public void setDexMethodIndex(int index) {
		method_dex_index_.write(index);
	}

	@Override public int getMethodIndex() {
		return method_index_.readInt();
	}

	@Override public void setMethodIndex(int index) {
		method_index_.write(index);
	}

	public void setFrameSizeInBytes(int index) {
		frame_size_in_bytes_.write(index);
	}

	public int getFrameSizeInBytes() {
		return frame_size_in_bytes_.readInt();
	}

	public void setDexCacheInitializedStaticStorage(int index) {
		dex_cache_initialized_static_storage_.write(index);
	}

	public int getDexCacheInitializedStaticStorage() {
		return dex_cache_initialized_static_storage_.readInt();
	}

	public void setVmapTable(int index) {
		vmap_table_.write(index);
	}

	public int getVmapTable() {
		return vmap_table_.readInt();
	}

	public void setCoreSpillMask(int index) {
		core_spill_mask_.write(index);
	}

	public int getCoreSpillMask() {
		return core_spill_mask_.readInt();
	}

	public void setFpSpillMask(int index) {
		fp_spill_mask_.write(index);
	}

	public int getFpSpillMask() {
		return fp_spill_mask_.readInt();
	}

	public void setMappingTable(int index) {
		mapping_table_.write(index);
	}

	public int getMappingTable() {
		return mapping_table_.readInt();
	}

	public void setCodeItemOffset(int index) {
		code_item_offset_.write(index);
	}

	public int getCodeItemOffset() {
		return code_item_offset_.readInt();
	}

	// public void set
}

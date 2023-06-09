// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: TipsConst.proto

package com.protos.TipsConst;

public final class TipsConst {
  private TipsConst() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code com.protos.TipsConst.Tips}
   */
  public enum Tips
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>UNKNOWN = 0;</code>
     */
    UNKNOWN(0),
    /**
     * <code>EQUIP_11 = 1;</code>
     */
    EQUIP_11(1),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>RUNNING = 1;</code>
     */
    public static final Tips RUNNING = EQUIP_11;
    /**
     * <code>UNKNOWN = 0;</code>
     */
    public static final int UNKNOWN_VALUE = 0;
    /**
     * <code>EQUIP_11 = 1;</code>
     */
    public static final int EQUIP_11_VALUE = 1;
    /**
     * <code>RUNNING = 1;</code>
     */
    public static final int RUNNING_VALUE = 1;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Tips valueOf(int value) {
      return forNumber(value);
    }

    public static Tips forNumber(int value) {
      switch (value) {
        case 0: return UNKNOWN;
        case 1: return EQUIP_11;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Tips>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Tips> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Tips>() {
            public Tips findValueByNumber(int number) {
              return Tips.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.protos.TipsConst.TipsConst.getDescriptor().getEnumTypes().get(0);
    }

    private static final Tips[] VALUES = {
      UNKNOWN, EQUIP_11, RUNNING, 
    };

    public static Tips valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Tips(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:com.protos.TipsConst.Tips)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017TipsConst.proto\022\024com.protos.TipsConst*" +
      "2\n\004Tips\022\013\n\007UNKNOWN\020\000\022\014\n\010EQUIP_11\020\001\022\013\n\007RU" +
      "NNING\020\001\032\002\020\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}

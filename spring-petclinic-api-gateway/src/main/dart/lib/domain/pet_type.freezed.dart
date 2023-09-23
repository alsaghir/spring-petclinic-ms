// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'pet_type.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

PetType _$PetTypeFromJson(Map<String, dynamic> json) {
  return _PetType.fromJson(json);
}

/// @nodoc
mixin _$PetType {
  int get id => throw _privateConstructorUsedError;
  String get name => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $PetTypeCopyWith<PetType> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $PetTypeCopyWith<$Res> {
  factory $PetTypeCopyWith(PetType value, $Res Function(PetType) then) =
      _$PetTypeCopyWithImpl<$Res, PetType>;
  @useResult
  $Res call({int id, String name});
}

/// @nodoc
class _$PetTypeCopyWithImpl<$Res, $Val extends PetType>
    implements $PetTypeCopyWith<$Res> {
  _$PetTypeCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? name = null,
  }) {
    return _then(_value.copyWith(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      name: null == name
          ? _value.name
          : name // ignore: cast_nullable_to_non_nullable
              as String,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$_PetTypeCopyWith<$Res> implements $PetTypeCopyWith<$Res> {
  factory _$$_PetTypeCopyWith(
          _$_PetType value, $Res Function(_$_PetType) then) =
      __$$_PetTypeCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({int id, String name});
}

/// @nodoc
class __$$_PetTypeCopyWithImpl<$Res>
    extends _$PetTypeCopyWithImpl<$Res, _$_PetType>
    implements _$$_PetTypeCopyWith<$Res> {
  __$$_PetTypeCopyWithImpl(_$_PetType _value, $Res Function(_$_PetType) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? name = null,
  }) {
    return _then(_$_PetType(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      name: null == name
          ? _value.name
          : name // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_PetType with DiagnosticableTreeMixin implements _PetType {
  const _$_PetType({required this.id, required this.name});

  factory _$_PetType.fromJson(Map<String, dynamic> json) =>
      _$$_PetTypeFromJson(json);

  @override
  final int id;
  @override
  final String name;

  @override
  String toString({DiagnosticLevel minLevel = DiagnosticLevel.info}) {
    return 'PetType(id: $id, name: $name)';
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties
      ..add(DiagnosticsProperty('type', 'PetType'))
      ..add(DiagnosticsProperty('id', id))
      ..add(DiagnosticsProperty('name', name));
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_PetType &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.name, name) || other.name == name));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(runtimeType, id, name);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_PetTypeCopyWith<_$_PetType> get copyWith =>
      __$$_PetTypeCopyWithImpl<_$_PetType>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_PetTypeToJson(
      this,
    );
  }
}

abstract class _PetType implements PetType {
  const factory _PetType({required final int id, required final String name}) =
      _$_PetType;

  factory _PetType.fromJson(Map<String, dynamic> json) = _$_PetType.fromJson;

  @override
  int get id;
  @override
  String get name;
  @override
  @JsonKey(ignore: true)
  _$$_PetTypeCopyWith<_$_PetType> get copyWith =>
      throw _privateConstructorUsedError;
}

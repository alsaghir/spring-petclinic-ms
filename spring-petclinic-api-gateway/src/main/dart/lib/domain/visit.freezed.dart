// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'visit.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

Visit _$VisitFromJson(Map<String, dynamic> json) {
  return _Visit.fromJson(json);
}

/// @nodoc
mixin _$Visit {
  int? get id => throw _privateConstructorUsedError;
  String get visitDate => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  int get petId => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $VisitCopyWith<Visit> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $VisitCopyWith<$Res> {
  factory $VisitCopyWith(Visit value, $Res Function(Visit) then) =
      _$VisitCopyWithImpl<$Res, Visit>;
  @useResult
  $Res call({int? id, String visitDate, String description, int petId});
}

/// @nodoc
class _$VisitCopyWithImpl<$Res, $Val extends Visit>
    implements $VisitCopyWith<$Res> {
  _$VisitCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? visitDate = null,
    Object? description = null,
    Object? petId = null,
  }) {
    return _then(_value.copyWith(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int?,
      visitDate: null == visitDate
          ? _value.visitDate
          : visitDate // ignore: cast_nullable_to_non_nullable
              as String,
      description: null == description
          ? _value.description
          : description // ignore: cast_nullable_to_non_nullable
              as String,
      petId: null == petId
          ? _value.petId
          : petId // ignore: cast_nullable_to_non_nullable
              as int,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$_VisitCopyWith<$Res> implements $VisitCopyWith<$Res> {
  factory _$$_VisitCopyWith(_$_Visit value, $Res Function(_$_Visit) then) =
      __$$_VisitCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({int? id, String visitDate, String description, int petId});
}

/// @nodoc
class __$$_VisitCopyWithImpl<$Res> extends _$VisitCopyWithImpl<$Res, _$_Visit>
    implements _$$_VisitCopyWith<$Res> {
  __$$_VisitCopyWithImpl(_$_Visit _value, $Res Function(_$_Visit) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = freezed,
    Object? visitDate = null,
    Object? description = null,
    Object? petId = null,
  }) {
    return _then(_$_Visit(
      id: freezed == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int?,
      visitDate: null == visitDate
          ? _value.visitDate
          : visitDate // ignore: cast_nullable_to_non_nullable
              as String,
      description: null == description
          ? _value.description
          : description // ignore: cast_nullable_to_non_nullable
              as String,
      petId: null == petId
          ? _value.petId
          : petId // ignore: cast_nullable_to_non_nullable
              as int,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_Visit with DiagnosticableTreeMixin implements _Visit {
  const _$_Visit(
      {required this.id,
      required this.visitDate,
      required this.description,
      required this.petId});

  factory _$_Visit.fromJson(Map<String, dynamic> json) =>
      _$$_VisitFromJson(json);

  @override
  final int? id;
  @override
  final String visitDate;
  @override
  final String description;
  @override
  final int petId;

  @override
  String toString({DiagnosticLevel minLevel = DiagnosticLevel.info}) {
    return 'Visit(id: $id, visitDate: $visitDate, description: $description, petId: $petId)';
  }

  @override
  void debugFillProperties(DiagnosticPropertiesBuilder properties) {
    super.debugFillProperties(properties);
    properties
      ..add(DiagnosticsProperty('type', 'Visit'))
      ..add(DiagnosticsProperty('id', id))
      ..add(DiagnosticsProperty('visitDate', visitDate))
      ..add(DiagnosticsProperty('description', description))
      ..add(DiagnosticsProperty('petId', petId));
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_Visit &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.visitDate, visitDate) ||
                other.visitDate == visitDate) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.petId, petId) || other.petId == petId));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode =>
      Object.hash(runtimeType, id, visitDate, description, petId);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$_VisitCopyWith<_$_Visit> get copyWith =>
      __$$_VisitCopyWithImpl<_$_Visit>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_VisitToJson(
      this,
    );
  }
}

abstract class _Visit implements Visit {
  const factory _Visit(
      {required final int? id,
      required final String visitDate,
      required final String description,
      required final int petId}) = _$_Visit;

  factory _Visit.fromJson(Map<String, dynamic> json) = _$_Visit.fromJson;

  @override
  int? get id;
  @override
  String get visitDate;
  @override
  String get description;
  @override
  int get petId;
  @override
  @JsonKey(ignore: true)
  _$$_VisitCopyWith<_$_Visit> get copyWith =>
      throw _privateConstructorUsedError;
}

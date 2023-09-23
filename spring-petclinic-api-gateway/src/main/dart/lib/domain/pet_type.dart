import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';

part 'pet_type.freezed.dart';
part 'pet_type.g.dart';

@freezed
class PetType with _$PetType {
  const factory PetType({
    required int id,
    required String name,
  }) = _PetType;

  factory PetType.fromJson(Map<String, Object?> json) =>
      _$PetTypeFromJson(json);
}

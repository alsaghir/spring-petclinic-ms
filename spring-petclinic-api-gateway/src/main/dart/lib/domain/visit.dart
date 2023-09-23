import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:flutter/foundation.dart';

part 'visit.freezed.dart';
part 'visit.g.dart';

@freezed
class Visit with _$Visit {
  const factory Visit({
    required int? id,
    required String visitDate,
    required String description,
    required int petId,
  }) = _Visit;

  factory Visit.fromJson(Map<String, Object?> json) => _$VisitFromJson(json);
}

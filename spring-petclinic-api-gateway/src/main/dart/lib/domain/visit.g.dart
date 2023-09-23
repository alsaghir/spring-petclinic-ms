// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'visit.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_Visit _$$_VisitFromJson(Map<String, dynamic> json) => _$_Visit(
      id: json['id'] as int?,
      visitDate: json['visitDate'] as String,
      description: json['description'] as String,
      petId: json['petId'] as int,
    );

Map<String, dynamic> _$$_VisitToJson(_$_Visit instance) => <String, dynamic>{
      'id': instance.id,
      'visitDate': instance.visitDate,
      'description': instance.description,
      'petId': instance.petId,
    };

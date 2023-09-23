import 'package:dio/dio.dart';

import 'pet.dart';

class Owner {
  int? id;
  String firstName;
  String lastName;
  String address;
  String city;
  String telephone;
  List<Pet> pets;

  Owner({
    this.id,
    required this.firstName,
    required this.lastName,
    required this.address,
    required this.city,
    required this.telephone,
    required this.pets,
  });

  factory Owner.from(Map<String, dynamic> json) => Owner(
        id: json["id"],
        firstName: json["firstName"],
        lastName: json["lastName"],
        address: json["address"],
        city: json["city"],
        telephone: json["telephone"],
        pets: Pet.fromJson(json["pets"]),
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "firstName": firstName,
        "lastName": lastName,
        "address": address,
        "city": city,
        "telephone": telephone,
        "pets": Pet.toJsonList(pets),
      };

  static List<Owner> fromJson(Response<List<dynamic>> list) {
    if (list.data == null) {
      return List<Owner>.empty();
    }
    return (list.data as List<dynamic>).map((e) => Owner.from(e)).toList();
  }
}

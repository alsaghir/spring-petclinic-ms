class Pet {
  int? id;
  String name;
  String birthDate;
  String typeName;
  int typeId;

  Pet(
      {this.id,
      required this.name,
      required this.birthDate,
      required this.typeName,
      required this.typeId});

  factory Pet.from(Map<String, dynamic> json) => Pet(
        id: json["id"],
        name: json["name"],
        birthDate: json["birthDate"],
        typeName: (json["type"] as Map)["name"],
        typeId: (json["type"] as Map)["id"],
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "name": name,
        "birthDate": birthDate,
        "typeName": typeName,
        "typeId": typeId
      };

  static List<Pet> fromJson(List<dynamic>? list) {
    if (list == null) {
      return List<Pet>.empty();
    }
    return list.map((e) => Pet.from(e)).toList();
  }

  static List<Map<String, dynamic>> toJsonList(List<Pet>? list) {
    return list == null ? List.empty() : list.map((e) => e.toJson()).toList();
  }
}

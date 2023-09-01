import 'package:dio/dio.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

import "../../conf/providers.dart";
import 'owner.dart';

class OwnersNotifier extends StateNotifier<List<Owner>> {
  OwnersNotifier({required this.httpClient}) : super(const []) {
    fetchAllOwners();
  }

  final Dio httpClient;
  List<Owner> owners = [];

  Future<void> fetchAllOwners() async {
    Response<List<dynamic>> data = await httpClient.get("http://localhost:7778/api/customer/owners",
        options: Options(contentType: Headers.jsonContentType));
    owners = Owner.fromJson(data);
    state = [...owners];
  }

  void filter(String nameSearchText) {
    List<String> names = nameSearchText.split(' ');
    state = owners
        .where((owner) =>
    nameSearchText.trim().isEmpty ||
        names.any((name) =>
            owner.firstName.toLowerCase().contains(name.toLowerCase())) ||
        names.any((name) =>
            owner.lastName.toLowerCase().contains(name.toLowerCase())))
        .toList();
  }
}

final ownersNotifierProvider =
StateNotifierProvider<OwnersNotifier, List<Owner>>((ref) {
  return OwnersNotifier(
    httpClient: ref.read(httpClientProvider),
  );
});

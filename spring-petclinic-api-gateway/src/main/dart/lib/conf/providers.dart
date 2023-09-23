import 'package:dio/dio.dart';
import 'package:flutter/services.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:yaml/yaml.dart';

import '../presentation/commons.dart';
import '../domain/owner.dart';
import '../domain/visit.dart';

part 'providers.g.dart';

final httpClientProvider = Provider<Dio>((ref) {
  return Dio();
});

final configProvider = Provider<Config?>((ref) {
  final AsyncValue<Config> config = ref.watch(asyncConfigProvider);

  return config.maybeWhen(orElse: () => null, data: (config) => config);
});

final asyncConfigProvider = FutureProvider<Config>((ref) async {
  final yamlString = await rootBundle.loadString('assets/config.yaml');
  final dynamic yamlMap = loadYaml(yamlString);

  String backendHost = getBackendHost(yamlMap);

  Config config = Config(
      kBackendHost: backendHost,
      kOwnersApi: yamlMap['api.owners'],
      kPetsVisitsApi: yamlMap['api.pets.visits'],
      kOwnersPetsApi: yamlMap['api.owners.pets'],
      kPetTypesApi: yamlMap['api.pet.types']);

  return config;
});

String getBackendHost(yamlMap) {
  String backendHost = "${Uri.base.scheme}://${Uri.base.host}:${Uri.base.port}";
  if (yamlMap is YamlMap && yamlMap.containsKey('backend.host')) {
    backendHost = yamlMap['backend.host'];
  }
  return backendHost;
}

@riverpod
Future<List<Visit>> visits(VisitsRef ref, ownerId) {
  final httpClient = ref.watch(httpClientProvider);
  final config = ref.watch(asyncConfigProvider.future);
  final Future<Owner> ownerWatcher = ref.watch(ownerProvider(ownerId).future);
  return ownerWatcher.then((owner) => config.then((conf) async {
        Response<List<dynamic>> response = await httpClient.get(
            conf.petsVisitsApiEndpoint(),
            queryParameters:
                Map.of({'petId': owner.pets.map((e) => e.id).join(',')}),
            options: Options(contentType: Headers.jsonContentType));
        return response.data!
            .map((e) => Visit.fromJson(e as Map<String, dynamic>))
            .toList();
      }));
}

final AutoDisposeFutureProviderFamily<Owner, int> ownerProvider =
    FutureProvider.autoDispose.family<Owner, int>((ref, ownerId) {
  final httpClient = ref.watch(httpClientProvider);
  final config = ref.watch(asyncConfigProvider.future);
  return config.then((conf) async {
    Response<Map<String, dynamic>> ownerResponse = await httpClient.get(
        "${conf.ownersEndpoint()}/$ownerId",
        options: Options(contentType: Headers.jsonContentType));
    final owner = Owner.from(ownerResponse.data as Map<String, dynamic>);
    return owner;
  });
});

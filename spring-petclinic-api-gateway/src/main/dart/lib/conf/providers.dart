import 'package:dio/dio.dart';
import 'package:flutter/services.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:yaml/yaml.dart';

import '../presentation/commons.dart';

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

  Config config =
      Config(kBackendHost: backendHost, kOwnersApi: yamlMap['api.owners']);

  return config;
});

String getBackendHost(yamlMap) {
  String backendHost = "${Uri.base.scheme}://${Uri.base.host}:${Uri.base.port}";
  if (yamlMap is YamlMap && yamlMap.containsKey('backend.host')) {
    backendHost = yamlMap['backend.host'];
  }
  return backendHost;
}

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:yaml/yaml.dart';

class Config {
  static late String kBackendHost;
  static late String kOwnersApi;
  static final Completer<bool> isLoaded = Completer<bool>();

  static String ownersEndpoint() {
    print(kBackendHost + kOwnersApi);
    return kBackendHost + kOwnersApi;
  }

  static loadAssets(BuildContext context) async {
    if (!isLoaded.isCompleted) {
      final yamlString =
          await DefaultAssetBundle.of(context).loadString('assets/config.yaml');
      final dynamic yamlMap = loadYaml(yamlString);
      print(yamlString);
      print(yamlMap);
      kBackendHost = yamlMap['backend.host'];
      kOwnersApi = yamlMap['api.owners'];
      isLoaded.complete(true);
    }
  }
}

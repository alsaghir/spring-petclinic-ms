import 'dart:async';

import 'package:flutter/material.dart';
import 'package:yaml/yaml.dart';

class Config {
  static late String kOwnersHost;
  static late String kOwnersApi;
  static final Completer<bool> isLoaded = Completer<bool>();

  static String ownersEndpoint() {
    return kOwnersHost + kOwnersApi;
  }

  static loadAssets(BuildContext context) async {
    if (!isLoaded.isCompleted) {
      final yamlString =
          await DefaultAssetBundle.of(context).loadString('assets/config.yaml');
      final dynamic yamlMap = loadYaml(yamlString);
      kOwnersHost = yamlMap['owners_host'];
      kOwnersApi = yamlMap['owners_api'];
      isLoaded.complete(true);
    }
  }
}

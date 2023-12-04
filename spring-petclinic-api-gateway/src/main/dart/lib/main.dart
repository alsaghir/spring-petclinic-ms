import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

import 'conf/providers.dart';
import 'conf/router.dart';

import 'presentation/commons.dart';

void main() {
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends HookConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    var router = getRouterConfig();

    AsyncValue<Config> config = ref.watch(asyncConfigProvider);

    return config.when(
        error: (error, stackTrace) => Text(error.toString()),
        loading: () => const Center(child: CircularProgressIndicator()),
        data: (data) => MaterialApp.router(
          theme: ThemeData.light(useMaterial3: false),
              debugShowCheckedModeBanner: false,
              routerConfig: router,
              title: "Pet Clinic App",
            ));
  }
}


import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:petclinicui/presentation/form_pet.dart';
import 'package:petclinicui/presentation/owner_details.dart';

import 'conf/providers.dart';
import 'presentation/form_visit.dart';
import 'presentation/app_screen.dart';
import 'presentation/commons.dart';
import 'presentation/home.dart';
import 'presentation/form_owner.dart';
import 'presentation/owners.dart';

void main() {
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends HookConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // GoRouter configuration
    var router = GoRouter(
      initialLocation: '/owners/5',
      debugLogDiagnostics: true,
      routes: [
        GoRoute(
          name: 'home',
          path: '/home',
          builder: (context, state) =>
              const AppScreen(tabContent: HomeScreen()),
        ),
        GoRoute(
            name: 'owners',
            path: '/owners',
            builder: (context, state) => const AppScreen(
                  tabContent: OwnersScreen(),
                ),
            routes: <GoRoute>[
              GoRoute(
                name: 'newOwner',
                path: 'new',
                builder: (context, state) => const AppScreen(
                  tabContent: NewOwnerScreen(
                    ownerId: null,
                  ),
                ),
              ),
              GoRoute(
                name: 'editOwner',
                path: 'edit/:id',
                builder: (context, state) => AppScreen(
                  tabContent:
                      NewOwnerScreen(ownerId: int.parse(state.pathParameters['id']!)),
                ),
              ),
              GoRoute(
                  name: 'viewOwner',
                  path: ':id',
                  builder: (context, state) => AppScreen(
                        tabContent: OwnerDetailsScreen(
                            ownerId: int.parse(state.pathParameters['id']!)),
                      ),
                  routes: <GoRoute>[
                    GoRoute(
                      name: 'addPet',
                      path: 'pets',
                      builder: (context, state) => AppScreen(
                        tabContent: FormPetScreen(
                            ownerId: int.parse(state.pathParameters['id']!),
                            petId: null),
                      ),
                    ),
                    GoRoute(
                      name: 'editPet',
                      path: 'pets/:petId',
                      builder: (context, state) => AppScreen(
                        tabContent: FormPetScreen(
                            ownerId: int.parse(state.pathParameters['id']!),
                            petId: int.parse(state.pathParameters['petId']!)),
                      ),
                    ),
                    GoRoute(
                      name: 'addVisit',
                      path: 'pets/:petId/visits/new',
                      builder: (context, state) => AppScreen(
                        tabContent: FormVisitScreen(
                            ownerId: int.parse(state.pathParameters['id']!),
                            petId: int.parse(state.pathParameters['petId']!)),
                      ),
                    ),
                  ]),
            ]),
        GoRoute(
          name: 'veterinarians',
          path: '/veterinarians',
          builder: (context, state) =>
              const AppScreen(tabContent: Text('Vater')),
        ),
        GoRoute(
          name: 'error',
          path: '/error',
          builder: (context, state) =>
              const AppScreen(tabContent: Text('Errors')),
        ),
      ],
    );

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

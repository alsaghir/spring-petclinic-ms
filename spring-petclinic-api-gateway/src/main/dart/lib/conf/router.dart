import 'package:go_router/go_router.dart';
import 'package:flutter/material.dart';

import '../presentation/app.dart';
import '../presentation/home.dart';
import '../presentation/owners.dart';
import '../presentation/form_owner.dart';
import '../presentation/form_pet.dart';
import '../presentation/owner_details.dart';
import '../presentation/form_visit.dart';

getRouterConfig() {
  return GoRouter(
      initialLocation: '/owners',
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
}
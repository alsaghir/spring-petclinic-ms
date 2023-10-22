import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:url_launcher/link.dart';

import '../conf/providers.dart';
import '../domain/owner.dart';
import '../domain/visit.dart';

class OwnerDetailsScreen extends HookConsumerWidget {
  const OwnerDetailsScreen({super.key, required this.ownerId});

  final int ownerId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<Owner> ownerWatcher = ref.watch(ownerProvider(ownerId));
    final AsyncValue<List<Visit>> visitsWatcher =
        ref.watch(visitsProvider(ownerId));

    return ownerWatcher.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stackTrace) => const Text("Error while fetching owner"),
      data: (owner) => visitsWatcher.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, stackTrace) =>
              const Text("Error while fetching visits"),
          data: (visits) {
            Map<int, List<Visit>> petsVisits = {
              for (var pet in owner.pets)
                pet.id!: visits.where((v) => pet.id == v.petId).toList(),
            };

            return Scaffold(
              appBar: AppBar(
                centerTitle: false,
                automaticallyImplyLeading: false,
                title: const Text('Owner Information'),
                leading: IconButton(
                  onPressed: () {
                    GoRouter.of(context).pop();
                  },
                  icon: const Icon(Icons.arrow_back),
                ),
              ),
              body: LayoutBuilder(
                builder: (context, constraints) => SingleChildScrollView(
                  child: ConstrainedBox(
                    constraints:
                        BoxConstraints(minHeight: constraints.maxHeight),
                    child: IntrinsicHeight(
                      child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 80.0),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const SizedBox(height: 50),
                            SizedBox(
                              height: 250,
                              child: DataTable(
                                columnSpacing:
                                    MediaQuery.of(context).size.width * 0.6,
                                headingRowHeight: 0,
                                columns: <DataColumn>[
                                  DataColumn(label: Container()),
                                  DataColumn(label: Container()),
                                ],
                                rows: <DataRow>[
                                  DataRow(
                                    cells: <DataCell>[
                                      const DataCell(Text('ID')),
                                      DataCell(Text(owner.id.toString())),
                                    ],
                                  ),
                                  DataRow(
                                    color: MaterialStateProperty.resolveWith<
                                            Color?>(
                                        (Set<MaterialState> states) =>
                                            Colors.grey.withOpacity(0.3)),
                                    cells: <DataCell>[
                                      const DataCell(Text('Name')),
                                      DataCell(Text(
                                          '${owner.firstName} ${owner.lastName}')),
                                    ],
                                  ),
                                  DataRow(
                                    cells: <DataCell>[
                                      const DataCell(Text('Address')),
                                      DataCell(Text(owner.address)),
                                    ],
                                  ),
                                  DataRow(
                                    color: MaterialStateProperty.resolveWith<
                                            Color?>(
                                        (Set<MaterialState> states) =>
                                            Colors.grey.withOpacity(0.3)),
                                    cells: <DataCell>[
                                      const DataCell(Text('City')),
                                      DataCell(Text(owner.city)),
                                    ],
                                  ),
                                  DataRow(
                                    cells: <DataCell>[
                                      const DataCell(Text('Telephone')),
                                      DataCell(Text(owner.telephone)),
                                    ],
                                  ),
                                ],
                              ),
                            ),
                            const SizedBox(height: 20),
                            Row(
                              children: [
                                OutlinedButton(
                                    style: ElevatedButton.styleFrom(
                                        backgroundColor:
                                            Theme.of(context).primaryColorDark,
                                        foregroundColor: Colors.white,
                                        padding: const EdgeInsets.all(20)),
                                    onPressed: () {
                                      GoRouter.of(context).pushNamed(
                                          "editOwner",
                                          pathParameters: {"id": "$ownerId"});
                                    },
                                    child: const Text(
                                      'Edit Owner',
                                      textScaleFactor: 1.5,
                                    )),
                                const SizedBox(width: 20),
                                OutlinedButton(
                                    style: ElevatedButton.styleFrom(
                                        backgroundColor:
                                            Theme.of(context).primaryColorDark,
                                        foregroundColor: Colors.white,
                                        padding: const EdgeInsets.all(20)),
                                    onPressed: () {
                                      GoRouter.of(context).pushNamed("addPet",
                                          pathParameters: {"id": "$ownerId"});
                                    },
                                    child: const Text(
                                      'Add New Pet',
                                      textScaleFactor: 1.5,
                                    )),
                              ],
                            ),
                            const SizedBox(height: 30),
                            const Text(
                              "Pets and Visits",
                              textScaleFactor: 2,
                            ),
                            const SizedBox(height: 30),
                            SizedBox(
                              height: owner.pets.length * 150,
                              child: ListView.separated(
                                  shrinkWrap: true,
                                  separatorBuilder: (context, index) =>
                                      SizedBox(
                                        child: Divider(
                                          height: 5,
                                          color: Theme.of(context)
                                              .dividerColor
                                              .withOpacity(0.5),
                                        ),
                                      ),
                                  itemCount: owner.pets.length,
                                  itemBuilder: (context, petIndex) => Row(
                                        children: [
                                          Flexible(
                                            flex: 1,
                                            child: ListTile(
                                              title: Text(
                                                  owner.pets[petIndex].name,
                                                  textScaleFactor: 1.5,
                                                  style: const TextStyle(
                                                      fontWeight:
                                                          FontWeight.bold)),
                                              subtitle: Column(
                                                children: <Widget>[
                                                  Row(
                                                    children: <Widget>[
                                                      const Text(
                                                          'Birth Date : ',
                                                          textScaleFactor: 1.5,
                                                          style: TextStyle(
                                                              fontWeight:
                                                                  FontWeight
                                                                      .bold)),
                                                      Text(
                                                        owner.pets[petIndex]
                                                            .birthDate,
                                                        textScaleFactor: 1.5,
                                                      ),
                                                    ],
                                                  ),
                                                  Row(
                                                    children: <Widget>[
                                                      const Text(
                                                        'Type : ',
                                                        textScaleFactor: 1.5,
                                                        style: TextStyle(
                                                            fontWeight:
                                                                FontWeight
                                                                    .bold),
                                                      ),
                                                      Text(
                                                        owner.pets[petIndex]
                                                            .typeName,
                                                        textScaleFactor: 1.5,
                                                      )
                                                    ],
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ),
                                          Flexible(
                                            flex: 1,
                                            child: Column(
                                              children: List.generate(
                                                petsVisits[owner
                                                            .pets[petIndex].id]
                                                        ?.length ??
                                                    0,
                                                (visitIndex) => ListTile(
                                                  title: Text(
                                                      "Visit Date: ${petsVisits[owner.pets[petIndex].id]![visitIndex].visitDate}"),
                                                  subtitle: Text(
                                                      "Description: ${petsVisits[owner.pets[petIndex].id]![visitIndex].description}"),
                                                ),
                                              ),
                                            ),
                                          ),
                                          Flexible(
                                            flex: 1,
                                            child: Column(
                                              children: [
                                                Link(
                                                  uri: Uri.parse(
                                                      GoRouter.of(context)
                                                          .namedLocation(
                                                              "editPet",
                                                          pathParameters: {
                                                        "id": "$ownerId",
                                                        "petId":
                                                            "${owner.pets[petIndex].id}"
                                                      })),
                                                  builder:
                                                      (context, followLink) =>
                                                          ElevatedButton(
                                                    autofocus: true,
                                                    onPressed: () {
                                                      GoRouter.of(context)
                                                          .pushNamed("editPet",
                                                          pathParameters: {
                                                            "id": "$ownerId",
                                                            "petId":
                                                                "${owner.pets[petIndex].id}"
                                                          });
                                                    },
                                                    child: const Text("Edit Pet"),
                                                  ),
                                                ),
                                                const SizedBox(
                                                  height: 15,
                                                ),
                                                Link(
                                                  uri: Uri.parse(
                                                      GoRouter.of(context)
                                                          .namedLocation(
                                                              "addVisit",
                                                          pathParameters: {
                                                        "id": "$ownerId",
                                                        "petId":
                                                            "${owner.pets[petIndex].id}"
                                                      })),
                                                  builder:
                                                      (context, followLink) =>
                                                          ElevatedButton(
                                                    autofocus: true,
                                                    onPressed: () {
                                                      GoRouter.of(context)
                                                          .pushNamed("addVisit",
                                                          pathParameters: {
                                                            "id": "$ownerId",
                                                            "petId":
                                                                "${owner.pets[petIndex].id}"
                                                          });
                                                    },
                                                    child: const Text("Add Visit"),
                                                  ),
                                                ),
                                              ],
                                            ),
                                          )
                                        ],
                                      )),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            );
          }),
    );
  }
}

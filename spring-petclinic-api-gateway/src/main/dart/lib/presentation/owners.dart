import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

import '../domain//owner_repo.dart';
import '../domain/owner.dart';
import '../presentation/commons.dart';

class OwnersScreen extends HookConsumerWidget {
  const OwnersScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: Commons.appBar(),
      body: Commons.futureBuilder(
        child: Column(
          children: [
            SearchOwnersWidget(),
            const OwnersListWidget(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          var r = GoRouter.of(context);
          fetchOwners() =>
              ref.read(ownersNotifierProvider.notifier).fetchAllOwners();
          r.addListener(fetchOwners);
          r.goNamed("newOwner");
        },
        tooltip: 'add owner',
        child: const Icon(Icons.add),
      ),
    );
  }
}

class OwnersListWidget extends StatefulHookConsumerWidget {
  const OwnersListWidget({Key? key}) : super(key: key);

  @override
  OwnersListWidgetState createState() => OwnersListWidgetState();
}

class OwnersListWidgetState extends ConsumerState<OwnersListWidget> {
  @override
  void initState() {
    super.initState();
    ref.read(ownersNotifierProvider.notifier).fetchAllOwners();
  }

  @override
  Widget build(BuildContext context) {
    final owners = ref.watch(ownersNotifierProvider);

    return owners.isEmpty
        ? const Center(child: Text('No record found'))
        : Expanded(
            child: ListView.separated(
                separatorBuilder: (context, index) => const SizedBox(
                      height: 5,
                    ),
                padding: const EdgeInsets.all(8),
                itemCount: owners.length,
                itemBuilder: (context, index) {
                  return ListTile(
                    // dense: true,
                    tileColor: Colors.amber.withOpacity(0.2),
                    contentPadding:
                        const EdgeInsets.symmetric(vertical: 0, horizontal: 5),
                    shape: RoundedRectangleBorder(
                      side: BorderSide(
                          width: 1, color: Colors.teal.withOpacity(0.7)),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    leading: CircleAvatar(
                      backgroundColor: Colors.teal.withOpacity(0.5),
                      child: Text(
                        '${index + 1}',
                        style: const TextStyle(color: Colors.black),
                      ),
                    ),
                    title: Text(
                      '${owners[index].firstName} ${owners[index].firstName}',
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                          fontSize: 14, fontWeight: FontWeight.w800),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('Address: '),
                            Text(
                              owners[index].address,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('City: '),
                            Text(
                              owners[index].city,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('Telephone: '),
                            Text(
                              owners[index].telephone,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                      ],
                    ),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                            onPressed: () {},
                            icon: const Icon(
                              Icons.edit,
                              color: Colors.green,
                              size: 16,
                            )),
                        IconButton(
                            onPressed: () {},
                            icon: const Icon(
                              Icons.delete,
                              color: Colors.red,
                              size: 16,
                            )),
                      ],
                    ),
                  );
                }),
          );
  }
}

class SearchOwnersWidget extends HookConsumerWidget {
  SearchOwnersWidget({super.key});

  final TextEditingController _searchController = TextEditingController();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Container(
        // Add padding around the search bar
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        // Use a Material design search bar
        child: TextFormField(
          textInputAction: TextInputAction.search,
          controller: _searchController,
          onFieldSubmitted: (value) {
            ref
                .watch(ownersNotifierProvider.notifier)
                .filter(_searchController.value.text);
          },
          decoration: InputDecoration(
            hintText: 'Search within first and last name then press Enter...',
            // Add a clear button to the search bar
            suffixIcon: IconButton(
              icon: const Icon(Icons.clear),
              onPressed: () => _searchController.clear(),
            ),
            // Add a search icon or button to the search bar
            prefixIcon: IconButton(
              icon: const Icon(Icons.search),
              onPressed: () {
                ref
                    .watch(ownersNotifierProvider.notifier)
                    .filter(_searchController.value.text);
              },
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(20.0),
            ),
          ),
        ),
      ),
    );
  }
}

class OwnerListState extends ChangeNotifier {
  List<Owner> _allOwners = List.empty();
  List<Owner> _currentOwners = List.empty();

  UnmodifiableListView<Owner> get owners =>
      UnmodifiableListView(_currentOwners);

  void filter(String nameSearchText) {
    List<String> names = nameSearchText.split(' ');
    _currentOwners = _allOwners
        .where((owner) =>
            nameSearchText.trim().isEmpty ||
            names.any((name) =>
                owner.firstName.toLowerCase().contains(name.toLowerCase())) ||
            names.any((name) =>
                owner.lastName.toLowerCase().contains(name.toLowerCase())))
        .toList();

    // This call tells the widgets that are listening to this model to rebuild.
    notifyListeners();
  }

  void allOwners(List<Owner> fromJson) {
    _allOwners = fromJson;
    _currentOwners = _allOwners;

    // This call tells the widgets that are listening to this model to rebuild.
    notifyListeners();
  }
}

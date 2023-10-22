import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../conf/providers.dart';
import '../domain/pet.dart';
import '../presentation/commons.dart';
import '../domain/pet_type.dart';

part 'form_pet.g.dart';

@riverpod
Future<List<PetType>> petTypes(PetTypesRef ref) {
  final httpClient = ref.watch(httpClientProvider);
  Future<Config> config = ref.watch(asyncConfigProvider.future);
  return config.then((conf) async {
    Response<List<dynamic>> response = await httpClient.get(
        conf.petTypesApiEndpoint(),
        options: Options(contentType: Headers.jsonContentType));
    return response.data!
        .map((e) => PetType.fromJson(e as Map<String, dynamic>))
        .toList();
  });
}

final AutoDisposeFutureProviderFamily<Pet?, OwnerPet> petToEditProvider =
    FutureProvider.autoDispose.family<Pet?, OwnerPet>((ref, ownerPet) {
  if (ownerPet.petId == null) return null;
  final httpClient = ref.watch(httpClientProvider);
  final config = ref.watch(asyncConfigProvider.future);
  return config.then((conf) async {
    Response<Map<String, dynamic>> response = await httpClient.get(
        "${conf.ownersPetsApiEndpoint()}/${ownerPet.ownerId}/pets/${ownerPet.petId}",
        options: Options(contentType: Headers.jsonContentType));
    return Pet.from(response.data as Map<String, dynamic>);
  });
});

/// https://docs.flutter.dev/cookbook/forms
class FormPetScreen extends StatefulHookConsumerWidget {
  const FormPetScreen({required this.ownerId, required this.petId, super.key});

  final int? petId;
  final int ownerId;

  @override
  FormPetScreenState createState() => FormPetScreenState();
}

class FormPetScreenState extends ConsumerState<FormPetScreen> {
  final _formKey = GlobalKey<FormState>();

  final _nameController = TextEditingController();
  final _dateController = TextEditingController();
  final _typeController = TypeController();

  late OwnerPet ownerPet;

  final StateProvider<bool> loadingSubmitFormProvider =
      StateProvider<bool>((ref) => false);

  @override
  void initState() {
    super.initState();
    ownerPet = OwnerPet(widget.ownerId, widget.petId);
  }

  @override
  Widget build(BuildContext context) {
    final config = ref.watch(configProvider);
    final dio = ref.watch(httpClientProvider);
    final isLoading = ref.watch(loadingSubmitFormProvider);
    AsyncValue<List<PetType>> petTypes = ref.watch(petTypesProvider);
    AsyncValue<Pet?> petForEdit = ref.watch(petToEditProvider(ownerPet));

    return petForEdit.when(
      error: (error, stackTrace) =>
          const Text("Error while fetching pet types"),
      loading: () => const Center(child: CircularProgressIndicator()),
      data: (petData) => petTypes.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stackTrace) =>
            const Text("Error while fetching pet types"),
        data: (loadedPetTypes) {
          if (petData != null && _nameController.text.isEmpty) {
            _nameController.text = petData.name;
            _dateController.text = petData.birthDate;
            _typeController.selectedPetType =
                loadedPetTypes.firstWhere((pt) => pt.id == petData.typeId);
          }

          return Scaffold(
            appBar: AppBar(
              centerTitle: false,
              automaticallyImplyLeading: false,
              title: const Text('Create new pet'),
              leading: IconButton(
                onPressed: () {
                  GoRouter.of(context).pop();
                },
                icon: const Icon(Icons.arrow_back),
              ),
            ),
            body: Align(
              alignment: Alignment.center,
              child: FractionallySizedBox(
                widthFactor: 0.5,
                child: ListView(
                  // Prevent performance issues, use
                  // Listview.builder() or CustomScrollView for optimization
                  shrinkWrap: false,
                  padding: const EdgeInsets.symmetric(
                      horizontal: 16.0, vertical: 2 * 16.0),
                  physics: const BouncingScrollPhysics(),
                  children: [
                    Form(
                      key: _formKey,
                      autovalidateMode: AutovalidateMode.onUserInteraction,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          CustomListTile(
                            nameController: _nameController,
                            validator: (value) => value == null || value.isEmpty
                                ? "Pet name is required"
                                : null,
                            labelText: 'Pet Name',
                            hintText: 'Enter name of the pet...',
                            icon: const Icon(Icons.pets),
                            textInputType: TextInputType.name,
                          ),
                          const SizedBox(height: 16.0),
                          CustomListTile(
                            readOnly: true,
                            nameController: _dateController,
                            validator: (value) => value == null || value.isEmpty
                                ? "Birth date is required"
                                : null,
                            labelText: 'Birth Date',
                            hintText: 'Click to select the birth date...',
                            icon: const Icon(Icons.date_range_outlined),
                            textInputType: TextInputType.name,
                            onTap: () async {
                              // Stop keyboard from appearing
                              FocusScope.of(context).requestFocus(FocusNode());

                              var date = await showDatePicker(
                                  context: context,
                                  initialEntryMode:
                                      DatePickerEntryMode.calendarOnly,
                                  initialDatePickerMode: DatePickerMode.day,
                                  initialDate: DateTime.now(),
                                  firstDate: DateTime(1960),
                                  lastDate: DateTime.now());

                              if (date == null) return;

                              _dateController.text =
                                  "${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
                            },
                          ),
                          const SizedBox(height: 16.0),
                          CustomListTile(
                            readOnly: true,
                            nameController: _typeController,
                            validator: (value) => value == null || value.isEmpty
                                ? "Pet type is required"
                                : null,
                            labelText: 'Type',
                            hintText: 'Click to select the pet type...',
                            icon: const Icon(Icons.date_range_outlined),
                            textInputType: TextInputType.name,
                            suffixIcon: PopupMenuButton<String>(
                              icon: const Icon(Icons.arrow_drop_down),
                              onSelected: (ptId) {
                                _typeController.selectedPetType =
                                    loadedPetTypes.firstWhere(
                                        (pt) => int.parse(ptId) == pt.id);
                              },
                              itemBuilder: (BuildContext context) {
                                return loadedPetTypes
                                    .map<PopupMenuItem<String>>((pt) {
                                  return PopupMenuItem(
                                      value: pt.id.toString(),
                                      child: Text(pt.name));
                                }).toList();
                              },
                            ),
                          ),
                          const SizedBox(height: 16.0),
                          SizedBox(
                            width: double.infinity,
                            child: ElevatedButton.icon(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Theme.of(context).primaryColor,
                                foregroundColor: Colors.white,
                              ),
                              onPressed: isLoading
                                  ? null
                                  : () async {
                                      if (_formKey.currentState!.validate()) {
                                        ref
                                            .read(loadingSubmitFormProvider
                                                .notifier)
                                            .state = true;
                                        var pet = Pet(
                                            name: _nameController.text,
                                            birthDate: _dateController.text,
                                            typeName: _typeController
                                                ._selectedPetType!.name,
                                            typeId: _typeController
                                                ._selectedPetType!.id);

                                        Response response;

                                        try {
                                          var apiPath =
                                              "${config!.ownersPetsApiEndpoint()}/${widget.ownerId}/pets${petData != null ? '/${petData.id}' : ''}";

                                          response = await dio.post(apiPath,
                                              data: pet.toJson(),
                                              options: Options(
                                                  contentType:
                                                      Headers.jsonContentType));

                                          if (response.statusCode == 201 ||
                                              response.statusCode == 204) {
                                            ref.invalidate(
                                                ownerProvider(widget.ownerId));
                                            if (!context.mounted) return;
                                            GoRouter.of(context).pop();
                                          }
                                        } catch (err) {
                                          print(err);
                                        } finally {
                                          ref
                                              .read(loadingSubmitFormProvider
                                                  .notifier)
                                              .state = false;
                                        }
                                      }
                                    },
                              icon: !isLoading
                                  ? const Icon(
                                      Icons.save_rounded,
                                      size: 20,
                                    )
                                  : const SizedBox.shrink(),
                              label: !isLoading
                                  ? const Text('Save')
                                  : const SizedBox(
                                      height: 20,
                                      width: 20,
                                      child: CircularProgressIndicator(
                                        color: Colors.white,
                                        strokeWidth: 2,
                                      ),
                                    ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

class TypeController extends TextEditingController {
  PetType? _selectedPetType;

  TypeController() : super();

  set selectedPetType(PetType selectedPetType) {
    _selectedPetType = selectedPetType;
    text = selectedPetType.name;
  }
}

class CustomListTile extends StatelessWidget {
  const CustomListTile(
      {super.key,
      required TextEditingController nameController,
      FormFieldValidator<String>? validator,
      required String labelText,
      required String hintText,
      required Icon icon,
      required TextInputType textInputType,
      GestureTapCallback? onTap,
      bool? readOnly,
      Widget? suffixIcon})
      : _nameController = nameController,
        _validator = validator,
        _labelText = labelText,
        _hintText = hintText,
        _icon = icon,
        _textInputType = textInputType,
        _onTap = onTap,
        _readOnly = readOnly,
        _suffixIcon = suffixIcon;

  final TextEditingController _nameController;
  final FormFieldValidator<String>? _validator;
  final String _labelText;
  final String _hintText;
  final Icon _icon;
  final TextInputType _textInputType;
  final GestureTapCallback? _onTap;
  final bool? _readOnly;
  final Widget? _suffixIcon;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: SizedBox(
        height: double.infinity,
        child: _icon,
      ),
      title: Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Expanded(
            child: TextFormField(
              readOnly: _readOnly ?? false,
              obscureText: false,
              controller: _nameController,
              style: const TextStyle(fontSize: 18),
              keyboardType: _textInputType,
              validator: _validator,
              onTap: _onTap,
              decoration: InputDecoration(
                  labelText: _labelText,
                  hintText: _hintText,
                  enabledBorder: OutlineInputBorder(
                    borderSide: BorderSide(
                      color: Theme.of(context).primaryColor,
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderSide: BorderSide(
                      color: Theme.of(context).primaryColor,
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  errorBorder: OutlineInputBorder(
                    borderSide: BorderSide(
                      color: Colors.red.withOpacity(0.5),
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  focusedErrorBorder: OutlineInputBorder(
                    borderSide: BorderSide(
                      color: Colors.red.withOpacity(0.5),
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  filled: true,
                  contentPadding:
                      const EdgeInsetsDirectional.fromSTEB(16, 0, 0, 0),
                  suffixIcon: _suffixIcon),
            ),
          ),
        ],
      ),
    );
  }
}

class OwnerPet {
  final int ownerId;
  final int? petId;

  OwnerPet(this.ownerId, this.petId);
}

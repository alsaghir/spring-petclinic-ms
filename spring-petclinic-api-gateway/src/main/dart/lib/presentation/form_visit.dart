import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'package:hooks_riverpod/hooks_riverpod.dart';

import '../conf/providers.dart';
import '../domain/visit.dart';

/// https://docs.flutter.dev/cookbook/forms
class FormVisitScreen extends StatefulHookConsumerWidget {
  const FormVisitScreen(
      {required this.ownerId, required this.petId, super.key});

  final int ownerId;
  final int petId;

  @override
  FormVisitScreenState createState() => FormVisitScreenState();
}

class FormVisitScreenState extends ConsumerState<FormVisitScreen> {
  final _formKey = GlobalKey<FormState>();

  final _dateController = TextEditingController();
  final _descriptionController = TextEditingController();
  final StateProvider<bool> loadingSubmitFormProvider =
      StateProvider<bool>((ref) => false);

  @override
  Widget build(BuildContext context) {
    final config = ref.watch(configProvider);
    final dio = ref.watch(httpClientProvider);
    final isLoading = ref.watch(loadingSubmitFormProvider);

    return Scaffold(
      appBar: AppBar(
        centerTitle: false,
        automaticallyImplyLeading: false,
        title: const Text('New Visit'),
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
                      nameController: _descriptionController,
                      validator: (value) => value == null || value.isEmpty
                          ? "Description is required"
                          : null,
                      labelText: 'Description',
                      hintText: 'Enter Description...',
                      icon: const Icon(Icons.text_snippet_outlined),
                      textInputType: TextInputType.name,
                    ),
                    const SizedBox(height: 16.0),
                    CustomListTile(
                      readOnly: true,
                      nameController: _dateController,
                      validator: (value) => value == null || value.isEmpty
                          ? "Visit date is required"
                          : null,
                      labelText: 'Date',
                      hintText: 'Click to select the visit date...',
                      icon: const Icon(Icons.date_range_outlined),
                      textInputType: TextInputType.name,
                      onTap: () async {
                        // Stop keyboard from appearing
                        FocusScope.of(context).requestFocus(FocusNode());

                        var date = await showDatePicker(
                            context: context,
                            initialEntryMode: DatePickerEntryMode.calendarOnly,
                            initialDatePickerMode: DatePickerMode.day,
                            initialDate: DateTime.now(),
                            firstDate: DateTime.now(),
                            lastDate: DateTime(DateTime.now().year + 1));

                        if (date == null) return;

                        _dateController.text =
                            "${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
                      },
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
                                      .read(loadingSubmitFormProvider.notifier)
                                      .state = true;

                                  var visit = Visit(
                                      id: null,
                                      description: _descriptionController.text,
                                      visitDate: _dateController.text,
                                      petId: widget.petId);

                                  Response response;

                                  try {
                                    response = await dio.post(
                                        "${config!.visitsOwnersEndpoint()}/${widget.ownerId}/pets/${widget.petId}/visits",
                                        data: visit.toJson(),
                                        options: Options(
                                            contentType:
                                                Headers.jsonContentType));

                                    if (response.statusCode == 201 ||
                                        response.statusCode == 204) {
                                      ref.invalidate(
                                          visitsProvider(widget.ownerId));
                                      if (!context.mounted) return;
                                      GoRouter.of(context).pop();
                                    }
                                  } catch (err) {
                                    print(err);
                                  } finally {
                                    ref
                                        .read(
                                            loadingSubmitFormProvider.notifier)
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
